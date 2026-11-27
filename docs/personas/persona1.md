# Persona 1 — Estafeta (End-User)

## 1. Perfil da Persona

* **Nome:** André Pereira
* **Idade:** 31 anos
* **Ocupação:** Estafeta de entregas (Uber Eats / Glovo / encomendas)
* **Perfil Tecnológico:** Usa o telemóvel constantemente durante o trabalho (no suporte do guiador); precisa de uma aplicação rápida, intuitiva e com poucos cliques para não perder tempo entre entregas.
* **Contexto e Motivação:**
  O André não tem carro nem mota — o custo de compra, combustível, seguro e manutenção é demasiado elevado — e na sua zona há poucos transportes públicos, com horários limitados. Precisa de um meio de transporte rápido, elétrico (para reduzir o esforço físico nas subidas e manter o ritmo), amigo do ambiente e de fácil acesso para conseguir trabalhar em turnos contínuos de 8 horas ou mais. Como o seu rendimento depende do número de entregas concluídas, o que mais valoriza é a continuidade do serviço sem interrupções por falta de bateria ou avarias.
* **Objetivos:**
  * Ter uma bicicleta elétrica carregada disponível no início do turno.
  * Conseguir trocar rapidamente de bicicleta a meio do dia sem cancelar o serviço nem perder tempo.
  * Proteger a bicicleta durante paragens curtas em restaurantes e prédios.
  * Saber sempre onde existem hubs próximos com bicicletas carregadas (>= 60%) e onde há espaço para devolver no fim do turno.
* **Frustrações / Dores:**
  * Risco de ficar sem bateria a meio de uma entrega.
  * Chegar a um hub para devolver a bicicleta e não haver espaço livre.
  * Encontrar bicicletas danificadas nos hubs que ninguém reportou.

---

## 2. Cenário Principal (Happy Path)

**Contexto:** Dia normal de trabalho do André, em turno contínuo das **09:00 às 19:00**.

1. **Início do dia normal (08:30) — Consulta e Reserva:**
   Antes de começar a trabalhar, o André abre a aplicação Mober para iniciar a sua utilização. O mapa mostra os hubs perto dele com bicicletas disponíveis (apenas as que cumprem a carga mínima operacional, `bateria >= 60%`, ordenadas pela maior carga) e indica quantas bicicletas existem em cada hub. Ele escolhe o hub mais próximo, seleciona uma bicicleta com carga elevada e faz a reserva (válida por no máximo **30 minutos**). Ao reservar, é validado o método de pagamento e retida a respetiva caução no gateway de pagamentos.

2. **Levantamento e Início do Serviço (08:55):**
   O André chega ao hub dentro da janela dos 30 minutos, desbloqueia a bicicleta reservada através da aplicação e inicia oficialmente o seu turno de trabalho.

3. **Ciclo Repetível de Entregas:**
   * **3.1 Recolha no Restaurante:** Ao chegar a um restaurante, o André estaciona a bicicleta, ativa o **Bloqueio Temporário** na aplicação (imobilizando eletronicamente o motor e as rodas) e entra no restaurante para recolher o pedido. Ao regressar, abre a aplicação e clica em **"Desbloquear / Retomar Viagem"**, seguindo para o cliente.
   * **3.2 Entrega ao Cliente:** Ao chegar à morada do cliente, volta a ativar o **Bloqueio Temporário** na aplicação enquanto sobe ao prédio para entregar o pedido. Ao voltar, desativa o bloqueio na aplicação e fica pronto para a entrega seguinte.

4. **Troca de Bicicleta a Meio do Turno (13:30):**
   Após várias horas de uso intensivo, a telemetria deteta que a bateria atingiu o limiar crítico (**10%**) e o André recebe uma notificação na aplicação (sendo que aos **5%** o motor elétrico é cortado automaticamente para preservar o GPS e a comunicação). A aplicação indica-lhe qual o hub mais próximo com uma bicicleta disponível com carga **>= 60%**. O André dirige-se ao hub, entrega a bicicleta descarregada (encerrando a utilização atual com o respetivo cálculo por quilómetro) e inicia viagem com a nova bicicleta para continuar o trabalho.

5. **Continuação do Turno:**
   O André repete os passos **3.1** e **3.2** durante a tarde com a nova bicicleta.

6. **Final do Dia e Devolução (18:45):**
   Ao terminar o turno, o André indica na aplicação que pretende encerrar o serviço e devolver a bicicleta. A aplicação apresenta os hubs próximos com **lugares/docas disponíveis** (ou sugere o mais próximo com vaga). O André desloca-se ao hub, tranca a bicicleta e finaliza a sessão. Somente neste momento final é calculado o custo da utilização (`distância percorrida * preço por km`), retirando-se esse valor à conta do utilizador (com dedução/libertação da caução) e registando a viagem no seu histórico.

---

## 3. Variantes e Cenários Extra (Exceções)

### Variante A — Expiração da Reserva Inicial (No-Show após 30 min)
* **Contexto:** O André reserva uma bicicleta às 08:30, mas atrasa-se a sair de casa e não chega ao hub até às 09:00.
* **Comportamento:** Ao atingir os **30 minutos**, a reserva expira automaticamente e a bicicleta volta ao estado `Available` para outros utilizadores poderem usá-la.

### Cenário Extra 1 — Falha na Bicicleta (Avaria nos Travões)
* **Contexto:** Durante a tarde, o André deteta uma falha nos travões.
* **Comportamento:** O André abre a aplicação, clica no botão de **Reportar Avaria** e preenche o motivo do problema (abrindo um ticket de manutenção). A aplicação mostra os hubs próximos com bicicletas disponíveis (>= 60%) para ele poder trocar. O André dirige-se ao hub, coloca a bicicleta avariada na doca — momento em que o sistema a bloqueia imediatamente no estado **`Maintenance`** (para que nenhum outro utilizador a possa escolher) — e levanta outra bicicleta operacional para continuar o trabalho.

---

## 4. User Stories (`US-EST-*`)

| ID | Origem no Cenário | User Story (Como / Quero / Para) | Critérios de Aceitação |
| :--- | :--- | :--- | :--- |
| **US-EST-01** | Passo 1 | **Como** estafeta, **quero** ver no mapa os hubs próximos com bicicletas carregadas (>= 60%) e a respetiva bateria, **para** escolher um hub com autonomia garantida para o meu turno. | • Apenas bicicletas acima do mínimo operacional (>= 60%) aparecem disponíveis para reserva.<br>• Hubs mostram contagem de veículos disponíveis e % de bateria. |
| **US-EST-02** | Passo 1 & Var. A | **Como** estafeta, **quero** reservar 1 bicicleta num hub por um máximo de 30 minutos, **para** garantir que o veículo está disponível quando chegar ao posto. | • Máximo 1 reserva normal ativa por utilizador.<br>• Reserva expira automaticamente após 30 minutos sem levantamento. |
| **US-EST-03** | Passo 1, 2 & 6 | **Como** estafeta, **quero** autorizar a caução na reserva e pagar no final da utilização consoante a distância percorrida (`distância * preço/km`), consultando o resumo de utilizações e cobranças, **para** gerir os meus custos de trabalho de forma transparente. | • Retenção de caução via payment sandbox.<br>• Cálculo no final da utilização ao parar num hub (`distância percorrida * preço/km`), débito na conta do utilizador e registo no dashboard. |
| **US-EST-04** | Passo 3.1 & 3.2 | **Como** estafeta, **quero** ativar e desativar o bloqueio temporário (motor e rodas) na aplicação durante paragens curtas, **para** recolher e entregar pedidos em segurança sem terminar o meu serviço. | • Estado alterna entre `In Use` e bloqueio temporário (`Paused/Locked`).<br>• Motor e rodas imobilizados; retoma imediata sem nova reserva. |
| **US-EST-05** | Passo 4 | **Como** estafeta em viagem, **quero** receber um aviso de bateria baixa (10%) e estar protegido pelo corte automático do motor aos 5%, **para** planear a troca antes de ficar sem comunicação ou GPS. | • Telemetria dispara notificação ao atingir o limiar de aviso (10%).<br>• Aos 5% de bateria, o motor elétrico é desligado automaticamente para preservar GPS e comunicação. |
| **US-EST-06** | Passo 4 | **Como** estafeta, quando a bateria da bicicleta que estou a usar passa abaixo do limiar crítico (10%, ou seja, quando dá a notificação), **quero** poder saber qual o hub perto de mim com uma bicicleta com bateria >= 60% disponível para utilizar, **para** efetuar a troca rapidamente sem ficar apeado. | • Ao atingir o limiar crítico (10%), apresenta os hubs próximos com bicicletas `Available` e bateria >= 60%.<br>• Mantém o limite de 1 reserva ativa em simultâneo. |
| **US-EST-07** | Passo 6 | **Como** estafeta, **quero** saber quais os hubs próximos com vagas disponíveis quando pretendo devolver a bicicleta, **para** não me deslocar a um posto cheio no final do turno. | • Mostra apenas hubs com lotação < 100% para devolução.<br>• Impede encerrar viagem num hub lotado. |
| **US-EST-08** | Extra 1 | **Como** estafeta, **quero** reportar uma avaria abrindo um ticket e bloquear a bicicleta ao devolvê-la no hub, **para** poder trocar de bicicleta e impedir que outros utilizadores a escolham. | • Report obriga a selecionar/descrever motivo.<br>• Ao entregar no hub, veículo passa imediatamente a `Maintenance` (indisponível para reserva) e gera ticket de SLA. |
