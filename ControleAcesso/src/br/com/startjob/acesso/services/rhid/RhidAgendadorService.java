package br.com.startjob.acesso.services.rhid;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Singleton
@Startup
public class RhidAgendadorService {

    @Resource
    private TimerService timerService;

    // Se o RhidService não for um bean gerenciado, você pode instanciá-lo manualmente
    private RhidService rhidService = new RhidService("https://rhid.com.br/v2");

    // Aqui você injeta seu DAO/Repository para buscar os domínios do banco
    // @Inject
    // private DominioRepository dominioRepository;

    @PostConstruct
    public void init() {
        System.out.println("Serviço de Agendamento RHiD inicializado.");
        // Opcional: Recriar timers ao reiniciar o servidor baseando-se no banco de dados.
    }

    /**
     * Método chamado pela tela JSF para configurar o agendamento
     */
    public void agendarRotina(long intervaloMinutos) {
        // Limpa agendamentos antigos para não duplicar
        for (Timer timer : timerService.getTimers()) {
            if ("RHID_TIMER".equals(timer.getInfo())) {
                timer.cancel();
            }
        }

        // Exemplo: cria um timer que roda a cada X minutos (para testes)
        // Para CRON complexos, use timerService.createCalendarTimer(...)
        timerService.createIntervalTimer(0, intervaloMinutos * 60 * 1000, new TimerConfig("RHID_TIMER", false));
        System.out.println("Rotina RHiD agendada para rodar a cada " + intervaloMinutos + " minutos.");
    }

    /**
     * O núcleo da rotina. Isso roda automaticamente quando o Timer dispara.
     */
    @Timeout
    public void executarExtracaoAfd(Timer timer) {
        System.out.println("Iniciando extração em lote do RHiD...");

        // 1. Busca as configurações globais do banco de dados
        String emailGlobal = "desenvolvimento@smartempresarial.com.br"; // Exemplo vindo do banco
        String senhaGlobal = "123456"; 
        
        // 2. Calcula o período (Exemplo: Mês atual)
        String dataFim = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String dataIni = LocalDate.now().withDayOfMonth(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 3. Busca apenas a lista de domínios e seus IDs no banco
        // List<DominioAfdDTO> dominios = dominioRepository.findAll();
        String[][] dominios = {
            {"direcional1", "[1]"},
            {"direcional2", "[1, 2]"},
            {"smartponto", "[1]"}
        };

        for (String[] dom : dominios) {
            String nomeDominio = dom[0];
            String idsEmpresas = dom[1];

            try {
                System.out.println("Processando domínio: " + nomeDominio);

                // Alimenta o serviço com a credencial global e o domínio da iteração atual.
                // Isso zera o 'tokenCache' antigo e força um novo login no próximo POST.
                rhidService.inicializarCredenciais(emailGlobal, senhaGlobal, nomeDominio);

                // Executa a extração
                String afdConteudo = rhidService.baixarAfd(dataIni, dataFim, idsEmpresas);
                
                // Salva o arquivo (Nomeando com o domínio para não sobrescrever)
                salvarArquivo("AFD_" + nomeDominio + "_" + dataFim + ".txt", afdConteudo);

            } catch (Exception e) {
                // Se um domínio der erro de senha ou timeout, loga e pula para o próximo
                System.err.println("Falha no domínio " + nomeDominio + ": " + e.getMessage());
            }
        }
    }

    private void salvarArquivo(String dominio, String conteudo) {
        // Implemente a lógica de salvar no HD, banco de dados, S3, etc.
        System.out.println("Arquivo do domínio " + dominio + " salvo com sucesso! Tamanho: " + conteudo.length());
    }
}