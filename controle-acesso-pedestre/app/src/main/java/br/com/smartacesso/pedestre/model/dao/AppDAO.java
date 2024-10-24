package br.com.smartacesso.pedestre.model.dao;

import android.content.Context;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.smartacesso.pedestre.model.dao.factories.MigrationDatabaseVersion;
import br.com.smartacesso.pedestre.model.dao.factories.PrimaryKeyFactory;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.Sort;

/**
 * Created by gustavo on 08/02/18.
 */

public abstract class AppDAO {

    protected Context context;
    protected Realm realm;


    public AppDAO(Context context){
        this.context = context;
        Realm.init(context);


        //verifica se existe necessidade
        //de atualização da base
        if(Realm.getDefaultConfiguration().getSchemaVersion()
                != MigrationDatabaseVersion.ACTUAL_VERSION) {
            RealmConfiguration config = new RealmConfiguration.Builder()
                    .schemaVersion(MigrationDatabaseVersion.ACTUAL_VERSION)
                    .migration(new MigrationDatabaseVersion())
                    .build();

            Realm.setDefaultConfiguration(config);

        }


        this.realm = Realm.getDefaultInstance();
    }

    public RealmObject findById(Class clazz, Long id) throws Exception {
        RealmObject obj = (RealmObject) clazz.cast(realm
                .where(clazz).equalTo("id", id).findFirst());
        if(obj != null)
            return realm.copyFromRealm(obj);
        return obj;
    }

    public void saveWithID(RealmObject object, Class clazz) throws Exception {

        if(realm.isInTransaction())
            realm.commitTransaction();

        realm.beginTransaction();

        //se existir campo "id" e ele vier nulo
        generateTempID(object, clazz);

        if(!onSave(object))
            return;

        //realiza gravação
        realm.copyToRealmOrUpdate(object);

        realm.commitTransaction();
    }

    public void save(RealmObject object, Class clazz) throws Exception {

        if(realm.isInTransaction())
            realm.commitTransaction();

        realm.beginTransaction();

        if(!onSave(object))
            return;

        //realiza gravação
        realm.copyToRealmOrUpdate(object);

        realm.commitTransaction();
    }

    public void generateTempID(RealmObject object, Class clazz) throws Exception {
        PrimaryKeyFactory.getInstance().initialize(realm);

        Method getId = clazz.getDeclaredMethod("getId", new Class[]{});
        if(getId != null){
            Long id = (Long) getId.invoke(object, new Object[]{});
            if(id == null){
                PrimaryKeyFactory.getInstance().initialize(realm);
                Long idItem = PrimaryKeyFactory.getInstance().nextKey(clazz);;

                Method setId = clazz.getDeclaredMethod("setId", new Class[]{Long.class});
                setId.invoke(object, idItem);
            }
        }

    }

    public boolean onSave(RealmObject object){
        return true;
    }

    public void remove(Class clazz, Long id) throws Exception {
        realm.beginTransaction();

        RealmObject obj = (RealmObject) clazz.cast(realm
                .where(clazz).equalTo("id", id).findFirst());
        if(obj != null)
            obj.deleteFromRealm();

        realm.commitTransaction();
    }

    public void remove(Class clazz, Long id, Map<String, Class> ignore) throws Exception {
        realm.beginTransaction();

        RealmObject obj = (RealmObject) clazz.cast(realm
                .where(clazz).equalTo("id", id).findFirst());

        //igone cascade itens
        igonaItens(obj, ignore);

        if(obj != null)
            obj.deleteFromRealm();

        realm.commitTransaction();
    }

    protected void igonaItens(RealmObject obj, Map<String, Class> ignore) throws Exception {
        for(String ig : ignore.keySet()) {
            Class classIgnore = ignore.get(ig);
            Field f = obj.getClass().getDeclaredField(ig);
            if(f != null){
                Object o = f.get(obj);
                if(o != null){
                    f.set(obj, null);
                }
            }
        }
    }

    public List<? extends RealmObject> findObjects(Class clazz,
                                                   HashMap<String, Object> parameters,
                                                   HashMap<String, Sort> sorts) throws Exception {
        RealmQuery model = realm.where(clazz);

        setParameters(parameters, model);
        setSorts(sorts, model);

        List<RealmObject> list = model.findAll();
        if(list != null && !list.isEmpty())
            return unmanagedAll(list, clazz);;

        return new ArrayList<RealmObject>();
    }

    public List<? extends RealmObject> findObjectsLimited(Class clazz,
                                                          HashMap<String, Object> parameters,
                                                          HashMap<String, Sort> sorts,
                                                          Integer start, Integer size) throws Exception {
        RealmQuery model = realm.where(clazz);

        setParameters(parameters, model);
        setSorts(sorts, model);

        List<RealmObject> list = model.findAll();
        if(list != null && !list.isEmpty()) {

            int finish = start + size;
            if(finish > list.size())
                finish = list.size();

            return unmanagedAll(list.subList(start, finish), clazz);

        }

        return new ArrayList<RealmObject>();
    }

    private void setSorts(HashMap<String, Sort> sorts, RealmQuery model) {

        if(sorts != null && !sorts.isEmpty()){
            String[] names = new String[sorts.keySet().size()];
            Sort   [] order = new Sort[sorts.keySet().size()];

            int i = 0;
            for (String key : sorts.keySet()) {
                names[i] = key;
                order[i] = sorts.get(key);
                i++;
            }

            model.sort(names, order);
        }
    }

    private void setParameters(HashMap<String, Object> parameters, RealmQuery model) {
        if(parameters != null && !parameters.isEmpty()){
            for (String key : parameters.keySet()) {
                Object p = parameters.get(key);
                if(key.contains("_not_null")) {
                    model.isNotNull(key.replace("_not_null",""));
                }else if(key.contains("_null")){
                    model.isNull(key.replace("_null",""));
                }else if(key.contains("_not_contains")){
                    model.not().contains(key.replace("_not_contains",""), (String)parameters.get(key), Case.SENSITIVE);
                }else if(key.contains("_contains")){
                    model.contains(key.replace("_contains",""), (String)parameters.get(key), Case.SENSITIVE);
                } else if(p instanceof Date){
                    if(key.contains("_menor")){
                        model.lessThan(key.replace("_menor",""), (Date)parameters.get(key));
                    }else if(key.contains("_maior")){
                        model.greaterThan(key.replace("_maior",""), (Date)parameters.get(key));
                    }else{
                        model.equalTo(key, (Date)parameters.get(key));
                    }
                }else if(p instanceof Long){
                    model.equalTo(key, (Long)parameters.get(key));
                }else if(p instanceof String){
                    model.equalTo(key, (String)parameters.get(key));
                }else if(p instanceof Integer){
                    model.equalTo(key, (Integer) parameters.get(key));
                }else if(p instanceof Boolean){
                    model.equalTo(key, (Boolean) parameters.get(key));
                }else if(p instanceof Long[]){
                    model.in(key, (Long[]) p);
                }
            }
        }
    }

    public List<? extends RealmObject> unmanagedAll(List<? extends RealmObject> objects, Class clazz) throws Exception {
        List<RealmObject> list = new ArrayList<RealmObject>();
        for (RealmObject obj : objects) {
            if(obj != null)
                list.add(realm.copyFromRealm(obj));
        }
        return list;

    }


}
