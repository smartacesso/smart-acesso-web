package br.com.smartacesso.pedestre.model.dao.factories;

import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Created by gustavo on 14/02/18.
 */

public class MigrationDatabaseVersion implements RealmMigration {

    public static final int ACTUAL_VERSION = 1;

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();


        /***
         *  Essa classe começara a ser utilizada depois
         *  de envio da versão para produção
         */

    }

    @Override
    public int hashCode() {
        return 37;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof MigrationDatabaseVersion);
    }
}
