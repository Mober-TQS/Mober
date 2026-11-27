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
  * Saber sempre onde existem hubs próximos com bicicletas carregadas (> 70%) e onde há espaço para devolver no fim do turno.
* **Frustrações / Dores:**
  * Risco de ficar sem bateria a meio de uma entrega.
  * Chegar a um hub para devolver a bicicleta e não haver espaço livre.
  * Encontrar bicicletas danificadas nos hubs que ninguém reportou.

---

## 2. Cenário Principal (Happy Path)

**Contexto:** Dia normal de trabalho do André, em turno contínuo das **09:00 às 19:00**.

1. **Início do dia normal (08:30) — Consulta e Reserva:**
   Antes de começar a trabalhar, o André abre a aplicação Mober para iniciar o seu serviço contínuo. O mapa mostra os hubs perto dele com bicicletas disponíveis (apenas as que cumprem a carga mínima operacional, ex.: bateria >= 70%, ordenadas pela maior carga) e indica quantas bicicletas existem em cada hub. Ele escolhe o hub mais próximo, seleciona uma bicicleta com carga elevada e faz a reserva (válida por no máximo **30 minutos**). Ao reservar/iniciar o serviço, é validado o método de pagamento e retida a respetiva caução/subscrição do turno no gateway de pagamentos.

2. **Levantamento e Início do Serviço (08:55):**
   O André chega ao hub dentro da janela dos 30 minutos, desbloqueia a bicicleta reservada através da aplicação e inicia oficialmente o seu turno de trabalho.

3. **Ciclo Repetível de Entregas:**
   * **3.1 Recolha no Restaurante:** Ao chegar a um restaurante, o André estaciona a bicicleta, ativa o **Bloqueio Temporário** na aplicação (imobilizando eletronicamente o motor e as rodas) e entra no restaurante para recolher o pedido. Ao regressar, abre a aplicação e clica em **"Desbloquear / Retomar Viagem"**, seguindo para o cliente.
   * **3.2 Entrega ao Cliente:** Ao chegar à morada do cliente, volta a ativar o **Bloqueio Temporário** na aplicação enquanto sobe ao prédio para entregar o pedido. Ao voltar, desativa o bloqueio na aplicação e fica pronto para a entrega seguinte.

4. **Troca de Bicicleta a Meio do Turno (13:30):**
   Após mais de 4 horas de uso intensivo, a telemetria deteta que a bateria está baixa e o André recebe um alerta na aplicação (ex.: aviso aos **25%**). Quando a bateria atinge o limiar de troca (ex.: **< 20%**, dando margem segura para concluir a entrega atual sem ficar apeado), a aplicação mostra os hubs mais próximos com bicicletas carregadas (> 70%) e desbloqueia a possibilidade de **reservar 1 bicicleta substituta enquanto ainda conduz a atual**. O André reserva a próxima bicicleta, dirige-se ao hub, entrega a bicicleta descarregada e levanta de imediato a nova bicicleta numa troca rápida, mantendo o mesmo turno contínuo ativo.

5. **Continuação do Turno:**
   O André repete os passos **3.1** e **3.2** durante a tarde com a nova bicicleta.

6. **Final do Dia e Devolução (18:45):**
   Ao terminar o turno, o André indica na aplicação que pretende encerrar o serviço e devolver a bicicleta. A aplicação apresenta os hubs próximos com **lugares/docas disponíveis** (ou sugere o mais próximo com vaga). O André desloca-se ao hub, tranca a bicicleta e finaliza a sessão. O sistema calcula o valor do serviço contínuo (ou eventual *overtime* se exceder o contratado), processa o pagamento final/libertação de caução e regista a viagem no seu histórico.

---

## 3. Variantes e Cenários Extra (Exceções)

### Variante A — Expiração da Reserva Inicial (No-Show após 30 min)
* **Contexto:** O André reserva uma bicicleta às 08:30, mas atrasa-se a sair de casa e não chega ao hub até às 09:00.
* **Comportamento:** Ao atingir os **30 minutos**, a reserva expira automaticamente e a bicicleta volta ao estado `Available` para outros utilizadores poderem usá-la.

### Cenário Extra 1 — Falha na Bicicleta (Avaria nos Travões)
* **Contexto:** Durante a tarde, o André deteta uma falha nos travões.
* **Comportamento:** O André abre a aplicação, clica no botão de **Reportar Avaria** e preenche o motivo do problema (abrindo um ticket de manutenção). A aplicação mostra os hubs próximos com bicicletas disponíveis (> 70%) para ele poder trocar. O André dirige-se ao hub, coloca a bicicleta avariada na doca — momento em que o sistema a bloqueia imediatamente no estado **`Maintenance`** (para que nenhum outro utilizador a possa escolher) — e levanta outra bicicleta operacional para continuar o trabalho.

### Cenário Extra 1.1 (Extremo) — Sem Bicicletas Disponíveis para Troca
* **Contexto:** O André recebe um alerta de bateria baixa (ou deteta uma avaria), mas nesse momento não existem bicicletas disponíveis com carga em nenhum hub próximo.
* **Comportamento:** A aplicação informa claramente o André de que não há veículos disponíveis para troca imediata e indica-lhe o **hub mais próximo com vaga livre** para devolver a bicicleta atual em segurança. O André devolve a bicicleta, o turno é encerrado antecipadamente (sem penalizações de *overtime*) e o pagamento é ajustado ao serviço utilizado.

---

## 4. User Stories (`US-EST-*`)

| ID | Origem no Cenário | User Story (Como / Quero / Para) | Critérios de Aceitação |
| :--- | :--- | :--- | :--- |
| **US-EST-01** | Passo 1 | **Como** estafeta, **quero** ver no mapa os hubs próximos com bicicletas carregadas (> 70%) e a respetiva bateria, **para** escolher um hub com autonomia garantida para o meu turno. | • Apenas bicicletas acima do mínimo operacional aparecem disponíveis.<br>• Hubs mostram contagem de veículos disponíveis e % de bateria. |
| **US-EST-02** | Passo 1 & Var. A | **Como** estafeta, **quero** reservar 1 bicicleta num hub por um máximo de 30 minutos, **para** garantir que o veículo está disponível quando chegar ao posto. | • Máximo 1 reserva normal ativa por utilizador.<br>• Reserva expira automaticamente após 30 minutos sem levantamento. |
| **US-EST-03** | Passo 1, 2 & 6 | **Como** estafeta, **quero** pagar/autorizar a caução pelo serviço contínuo e consultar o resumo de utilizações e cobranças no fim do turno, **para** gerir os meus custos de trabalho de forma transparente. | • Retenção de caução / início de sessão via payment sandbox.<br>• Cálculo de custo final e libertação de caução ao devolver; registo no dashboard do utilizador. |
| **US-EST-04** | Passo 3.1 & 3.2 | **Como** estafeta, **quero** ativar e desativar o bloqueio temporário (motor e rodas) na aplicação durante paragens curtas, **para** recolher e entregar pedidos em segurança sem terminar o meu serviço. | • Estado alterna entre `In Use` e bloqueio temporário (`Paused/Locked`).<br>• Motor e rodas imobilizados; retoma imediata sem nova reserva. |
| **US-EST-05** | Passo 4 | **Como** estafeta em viagem, **quero** receber um aviso de bateria baixa e ver hubs próximos com bicicletas carregadas (> 70%), **para** planear a troca antes de ficar sem bateria. | • Telemetria dispara notificação ao atingir o limiar de aviso.<br>• Mapa/lista sugere hubs próximos com bicicletas carregadas. |
| **US-EST-06** | Passo 4 | **Como** estafeta com bateria abaixo do limiar crítico (ex.: < 20%), **quero** poder reservar 1 bicicleta substituta enquanto ainda uso a atual, **para** garantir uma troca rápida sem interrupções. | • Reserva concorrente é recusada acima do limiar.<br>• Abaixo do limiar permite 1 reserva de troca; troca no hub transfere o turno sem nova caução. |
| **US-EST-07** | Passo 6 & Ext. 1.1 | **Como** estafeta, **quero** saber quais os hubs próximos com vagas disponíveis quando pretendo devolver a bicicleta, **para** não me deslocar a um posto cheio no final do turno. | • Mostra apenas hubs com lotação < 100% para devolução.<br>• Impede encerrar viagem num hub lotado. |
| **US-EST-08** | Extra 1 | **Como** estafeta, **quero** reportar uma avaria abrindo um ticket e bloquear a bicicleta ao devolvê-la no hub, **para** poder trocar de bicicleta e impedir que outros utilizadores a escolham. | • Report obriga a selecionar/descrever motivo.<br>• Ao entregar no hub, veículo passa imediatamente a `Maintenance` (indisponível para reserva) e gera ticket de SLA. |
| **US-EST-09** | Extra 1.1 | **Como** estafeta, **quero** ser notificado caso não existam bicicletas disponíveis para troca e ser encaminhado para um hub com vaga para devolução, **para** encerrar o serviço em segurança sem penalizações indevidas. | • Caso 0 bikes disponíveis para swap, apresenta aviso explícito + hub mais próximo com vaga.<br>• Encerra turno sem cobrança de *overtime*. |
