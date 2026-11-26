# Service Level Objective - Möber (SLOs)

---

## Índice

1. [SLO-01: Acessibilidade da Interface Web (UI)](#slo-01-acessibilidade-da-interface-web-ui)
2. [SLO-02: Velocidade e Fluidez da Interface Web](#slo-02-velocidade-e-fluidez-da-interface-web)
3. [SLO-03: Autenticação, Sessão e Permissões (RBAC)](#slo-03-autenticação-sessão-e-permissões-rbac)
4. [SLO-04: Disponibilidade Global e Resistência sob Carga da API](#slo-04-disponibilidade-global-e-resistência-sob-carga-da-api)
5. [SLO-05: Descoberta de Frota e Mapa de Postos (Hubs)](#slo-05-descoberta-de-frota-e-mapa-de-postos-hubs)
6. [SLO-06: Gestão de Reservas, Trocas e Expiração](#slo-06-gestão-de-reservas-trocas-e-expiração)
7. [SLO-07: Mudanças de Estado, Devoluções e Reparações](#slo-07-mudanças-de-estado-devoluções-e-reparações)
8. [SLO-08: Pagamentos, Cauções e Faturação por Km](#slo-08-pagamentos-cauções-e-faturação-por-km)
9. [SLO-09: Telemetria MQTT, Gestão de Bateria e Métricas](#slo-09-telemetria-mqtt-gestão-de-bateria-e-métricas)

---

## SLO-01: Acessibilidade da Interface Web (UI)

* **O que garante:** A aplicação é utilizável por qualquer pessoa, incluindo pessoas com deficiências ou em condições difíceis de rua (ecrãs sob sol intenso, uso com luvas).
* **Regras principais:**
  * **Zero erros graves:** Conformidade WCAG 2.1 AA sem violações críticas/sérias no validador automático (`axe-core`).
  * **Navegação a 100% por teclado:** Capacidade de usar o sistema apenas com `Tab`, `Enter` e `Esc`, com foco visualmente destacado e sem ficar bloqueado em campos.
  * **Alvos táteis mínimos:** Todos os botões e áreas de toque no ecrã móvel têm pelo menos $44 \times 44\text{ px}$.
* **Critério de rejeição (CI/Release):** A build é bloqueada de imediato se surgir 1 erro grave de acessibilidade ou falha nos fluxos de teclado.

---

## SLO-02: Velocidade e Fluidez da Interface Web

* **O que garante:** Carregamento célere das páginas e desenho imediato dos marcadores no mapa.
* **Limites máximos:**
  * **App móvel (4G urbana simulada):** Conteúdo principal visível em $\le 2{,}5\text{ s}$.
  * **Portal de operações (desktop):** Conteúdo principal visível em $\le 2{,}0\text{ s}$.
  * **Pinos no mapa:** Marcadores aparecem no ecrã em $\le 1{,}5\text{ s}$ após resposta da API.
* **Critério de rejeição (CI):** Reprova a build se a mediana dos testes de velocidade exceder estes limiares.

---

## SLO-03: Autenticação, Sessão e Permissões (RBAC)

* **O que garante:** Processo de início de sessão seguro contra ataques, sessões estáveis para turnos de trabalho completos e verificação rigorosa de perfis.
* **Regras principais:**
  * **Login seguro e célere:** Encriptação de palavras-passe, respondendo em $\le 1{,}0\text{ s}$ no percentil 95 ($p95$).
  * **Turno ininterrupto:** As sessões de trabalho duram pelo menos $8\text{ h}$ sem desligar o utilizador.
  * **Validação instantânea:** O servidor valida os direitos de acesso em $\le 0{,}1\text{ s}$ ($100\text{ ms}$).
  * **Bloqueio total:** Rejeição rigorosa de contas suspensas ou acessos indevidos (códigos 401, 403 e 423).
* **Critério de rejeição:** A build chumba se ocorrer qualquer erro de servidor (`5xx`) no login ou se forem detetadas falhas de fuga de dados entre utilizadores.

---

## SLO-04: Disponibilidade Global e Resistência sob Carga da API

* **O que garante:** Os serviços mantêm-se online e rápidos mesmo durante picos repentinos de reservas e entregas.
* **Metas de desempenho:**
  * **Disponibilidade mensal:** No ar durante $\ge 99{,}9\%$ do tempo (máximo de 43 minutos de indisponibilidade por mês).
  * **Sob tráfego normal:** Criar reservas (`POST /bookings`) responde em $< 500\text{ ms}$ sem erros `5xx`.
  * **Sob picos súbitos de carga (150 a 200 utilizadores em simultâneo):**
    * Erros de servidor contidos abaixo de $1{,}0\%$.
    * Respostas mantidas abaixo de $1{,}0\text{ s}$.
    * Recuperação total para a velocidade normal em $\le 60\text{ s}$ após o pico terminar.
* **Critério de rejeição:** A versão é reprovada se falhar a recuperação em 60 segundos ou tiver erros de servidor sob carga normal.

---

## SLO-05: Descoberta de Frota e Mapa de Postos (Hubs)

* **O que garante:** Os utilizadores encontram bicicletas e postos livres no mapa sem demoras e com informação atualizada.
* **Regras principais:**
  * **Filtros visuais claros:** Identificação imediata de veículos elegíveis ($\ge 20\%$ de bateria), recomendados para turnos longos ($> 70\%$) e postos com espaço para devolução ($< 100\%$ cheios).
  * **Sem exposição de dados pessoais:** O mapa nunca mostra informação de clientes.
  * **Rapidez de consulta:** Respostas do mapa em $\le 450\text{ ms}$ ($p95$).
  * **Dados em tempo real:** A posição e estado no mapa nunca têm mais de $5\text{ s}$ de atraso face ao que os sensores transmitem.
* **Critério de rejeição:** Alerta se os tempos de resposta passarem os 450 ms ou se os dados ficarem desatualizados por mais de 5 segundos.

---

## SLO-06: Gestão de Reservas, Trocas e Expiração

* **O que garante:** Ninguém fica com a mesma bicicleta ao mesmo tempo e os prazos são cumpridos à risca.
* **Regras principais:**
  * **Zero reservas duplicadas (*double-booking*):** Tolerância zero para dois utilizadores reservarem o mesmo veículo ou para uma pessoa ter mais de uma reserva ativa.
  * **Rapidez na reserva:** Processamento em $\le 500\text{ ms}$ ($p95$).
  * **Troca facilitada:** Se a bicicleta tiver $< 20\%$ de carga ou apresentar avaria, a troca por outra é imediata, sem nova cobrança ou caução.
  * **Libertação ao fim de 30 minutos:** Se o utilizador não levantar a bicicleta reservada, o sistema liberta-a obrigatoriamente no prazo máximo de $60\text{ s}$ após o limite de 30 minutos.
* **Critério de rejeição:** Bloqueio imediato da versão se houver 1 única reserva duplicada ou atraso superior a 60 segundos na libertação automática.

---

## SLO-07: Mudanças de Estado, Devoluções e Reparações

* **O que garante:** Controlo rigoroso de cada fase da viagem (bloquear, desbloquear, avariar, devolver) e respeito pela lotação física dos postos.
* **Regras principais:**
  * **Rapidez operacional:** Mudanças de estado respondem em $\le 350\text{ ms}$ ($p95$).
  * **Segurança de bateria:** Bloqueio obrigatório de retomada de marcha se a bicicleta tiver $\le 5\%$ de bateria.
  * **Capacidade dos postos:** É impossível devolver uma bicicleta num posto que já esteja com a lotação a 100%.
  * **Registo de avarias:** Ao reportar uma falha mecânica/elétrica, a bicicleta passa de imediato a manutenção e abre um bilhete de reparação.
* **Critério de rejeição:** Reprovação no CI se o sistema permitir ultrapassar vagas de um posto ou retomar viagem com bateria crítica ($\le 5\%$).

---

## SLO-08: Pagamentos, Cauções e Faturação por Km

* **O que garante:** Cobranças sem enganos, sem cobranças duplicadas por falhas de rede e cálculo justo da viagem.
* **Regras principais:**
  * **Zero duplas cobranças:** O envio repetido do mesmo pedido com a mesma chave de identificação (*Idempotency-Key*) nunca resulta em dois débitos no cartão.
  * **Tempo de pagamento:** Respostas em $\le 1{,}2\text{ s}$ ($p95$), cortando ligações pendentes ao fim de $3\text{ s}$ para evitar bloqueios.
  * **Correção automática:** Pelo menos $99{,}9\%$ dos pagamentos ou cauções interrompidos por problemas de rede são regularizados automaticamente em $\le 5\text{ min}$.
  * **Preço por quilómetro real:** O valor da viagem baseia-se unicamente nos quilómetros medidos pela telemetria do servidor, aplicando o tarifário fixado no arranque da viagem.
* **Critério de rejeição:** Tolerância zero para faturas com quilómetros incorretos ou cobranças duplas.

---

## SLO-09: Telemetria MQTT, Gestão de Bateria e Métricas

* **O que garante:** O sistema recebe continuamente a posição e saúde das bicicletas e protege a integridade das baterias.
* **Regras principais:**
  * **Processamento contínuo:** $\ge 99{,}9\%$ das leituras de GPS e estado são gravadas na base de dados em menos de $5\text{ s}$.
  * **Aviso preventivo (10%):** Envio de notificação automática ao utilizador quando a bateria atinge os 10%.
  * **Corte de tração de emergência (5%):** O motor corta a assistência aos 5% para não esgotar a célula, mas o transmissor GPS continua a emitir para não perder a distância percorrida para faturação.
  * **Saúde das métricas:** O serviço de monitorização interna está disponível em $\ge 99{,}9\%$ das recolhas.
* **Critério de rejeição:** Alerta de prioridade máxima se a fila de mensagens perdidas crescer ou se o processamento demorar mais de 5 segundos.