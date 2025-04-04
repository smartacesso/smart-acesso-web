package br.com.startjob.acesso.controller.uc017;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import br.com.startjob.acesso.annotations.UseCase;
import br.com.startjob.acesso.controller.RelatorioController;
import br.com.startjob.acesso.modelo.BaseConstant;
import br.com.startjob.acesso.modelo.entity.AcessoEntity;
import br.com.startjob.acesso.modelo.entity.ParametroEntity;
import br.com.startjob.acesso.modelo.enumeration.TipoPedestre;

@SuppressWarnings("serial")
@Named("relatorioPermanenciaController")
@ViewScoped
@UseCase(classEntidade = AcessoEntity.class, funcionalidade = "Relatório de permanência", lazyLoad = true, quantPorPagina = 10)
public class RelatorioPermanenciaController extends RelatorioController {

    private boolean permiteCampoAdicionalCrachaMatricula = true;
    List<RelatorioPermanenciaDto> listaResultados = new ArrayList<>();

    @PostConstruct
    @Override
    public void init() {
        getParans().put("data_maior_data", formatarDataInicioDia(new Date()));
        getParans().put("data_menor_data", formatarDataFimDia(new Date()));
        buscar();
        ParametroEntity param = baseEJB.getParametroSistema(
                BaseConstant.PARAMETERS_NAME.PERMITIR_CAMPO_ADICIONAL_CRACHA, getUsuarioLogado().getCliente().getId());
        if (param != null)
            permiteCampoAdicionalCrachaMatricula = Boolean.valueOf(param.getValor());
    }

    @Override
    public String buscar() {
        Date dataInicio = (Date) super.getParans().get("data_maior_data");
        Date dataFim = (Date) super.getParans().get("data_menor_data");

        Calendar calInicio = Calendar.getInstance();
        calInicio.setTime(dataInicio);
        int mesInicio = calInicio.get(Calendar.MONTH);
        int anoInicio = calInicio.get(Calendar.YEAR);

        Calendar calFim = Calendar.getInstance();
        calFim.setTime(dataFim);
        int mesFim = calFim.get(Calendar.MONTH);
        int anoFim = calFim.get(Calendar.YEAR);

        if (mesInicio != mesFim || anoInicio != anoFim) {
            throw new IllegalArgumentException("O período selecionado deve estar dentro do mesmo mês.");
        }

        getParans().put("cliente.id", getUsuarioLogado().getCliente().getId());
        getParans().put("pedestre.tipo", TipoPedestre.PEDESTRE);
        setNamedQueryPesquisa("findAllComPedestreEmpresaECargo");
        super.buscar();
        List<AcessoEntity> acessos = super.getResult().stream()
                .map(e -> (AcessoEntity) e)
                .collect(Collectors.toList());

        listaResultados = calcularTempoTotalPorPedestre(acessos, mesInicio, anoInicio);
        return "";
    }

    private List<RelatorioPermanenciaDto> calcularTempoTotalPorPedestre(List<AcessoEntity> acessos, int mesRelatorio, int anoRelatorio) {
        Map<Long, RelatorioPermanenciaDto> mapaPermanencia = new HashMap<>();
        Map<Long, Date> ultimaEntradaPorPedestre = new HashMap<>();
        acessos.sort(Comparator.comparing(AcessoEntity::getData));

        for (AcessoEntity acesso : acessos) {
            Date data = acesso.getData();
            Calendar calAcesso = Calendar.getInstance();
            calAcesso.setTime(data);
            int mesAcesso = calAcesso.get(Calendar.MONTH);
            int anoAcesso = calAcesso.get(Calendar.YEAR);

            if (mesAcesso != mesRelatorio || anoAcesso != anoRelatorio) {
                continue;
            }

            Long idPedestre = acesso.getPedestre().getId();
            String sentido = acesso.getSentido();
            RelatorioPermanenciaDto dto = mapaPermanencia.computeIfAbsent(idPedestre, k -> {
                RelatorioPermanenciaDto novoDto = new RelatorioPermanenciaDto();
                novoDto.setIdPedestre(idPedestre);
                novoDto.setNome(acesso.getPedestre().getNome());
                novoDto.setCotaMensal(buscarCotaMensal(idPedestre, mesRelatorio, anoRelatorio));
                novoDto.setTempoTotal(0L);
                return novoDto;
            });

            if ("ENTRADA".equalsIgnoreCase(sentido)) {
                if (ultimaEntradaPorPedestre.containsKey(idPedestre)) {
                    System.out.println("Aviso: Entrada repetida ignorada para o pedestre " + dto.getNome());
                    continue; // Ignora essa entrada e mantém a anterior
                }
                // Registra a primeira entrada válida
                ultimaEntradaPorPedestre.put(idPedestre, data);

            } else if ("SAIDA".equalsIgnoreCase(sentido)) {
                if (!ultimaEntradaPorPedestre.containsKey(idPedestre)) {
                    System.out.println("Aviso: Saída sem entrada detectada para o pedestre " + dto.getNome() + ". Ignorando saída.");
                    continue;
                }

                Date entrada = ultimaEntradaPorPedestre.remove(idPedestre);

                if (entrada.before(data)) {
                    long segundos = (data.getTime() - entrada.getTime()) / 1000;
                    if (segundos > 0) {
                        dto.setTempoTotal(dto.getTempoTotal() + segundos);
                    }
                } else {
                    System.out.println("Erro: Entrada posterior à saída para " + dto.getNome() + ". Verifique os dados.");
                }
            }
        }

        // Processa pedestres que entraram e não saíram
        Date fimDoPeriodo = getFimDoDia((Date) super.getParans().get("data_maior_data"));
        for (Map.Entry<Long, Date> entradaPendente : ultimaEntradaPorPedestre.entrySet()) {
            Long idPedestre = entradaPendente.getKey();
            Date entrada = entradaPendente.getValue();
            long minutos = (fimDoPeriodo.getTime() - entrada.getTime()) / 60000;
            mapaPermanencia.get(idPedestre).setTempoTotal(mapaPermanencia.get(idPedestre).getTempoTotal() + minutos);

            System.out.println("⚠️ Aviso: Pedestre " + mapaPermanencia.get(idPedestre).getNome() + " entrou mas não saiu. Tempo calculado até o fim do período.");
        }

        // Calcula saldo e formata tempo
        for (RelatorioPermanenciaDto dto : mapaPermanencia.values()) {
            Long cotaMensal = dto.getCotaMensal() != null ? dto.getCotaMensal() : 0L;
            Long saldoRestante = cotaMensal - Math.abs(dto.getTempoTotal());

            dto.setSaldoRestante(saldoRestante);
            dto.setTempoTotalFormatado(formatarTempo(dto.getTempoTotal()));
            dto.setSaldoRestanteFormatado(formatarTempo(dto.getSaldoRestante()));
            dto.setCotaMensalFormatada(formatarTempo(dto.getCotaMensal()));
        }

        return new ArrayList<>(mapaPermanencia.values());
    }


    private Long buscarCotaMensal(Long idPedestre, int mes, int ano) {
        Long cota = super.buscaCotasPedestre(idPedestre, mes, ano);
        return (cota != null) ? cota * 3600 : 0L;
    }

    private Date getFimDoDia(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    private String formatarTempo(long segundos) {
        boolean negativo = segundos < 0;
        segundos = Math.abs(segundos); // Trabalha com valores positivos para cálculo

        long horas = (segundos / 60) / 60;
        long minRestantes = (segundos / 60) % 60;

        String tempoFormatado = String.format("%02d:%02d", horas, minRestantes);
        return negativo ? "-" + tempoFormatado : tempoFormatado; // Adiciona "-" se for negativo
    }


    public List<RelatorioPermanenciaDto> getListaResultados() {
        return listaResultados;
    }
}
