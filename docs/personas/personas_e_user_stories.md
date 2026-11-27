# Mober — Personas e User Stories (Estafeta, Operador de Terreno & Administrador)

## 1. Indicação das Personas

* **Persona 1 — Estafeta (End-User): André Pereira (31 anos)**
  Estafeta de entregas (Uber Eats / Glovo / encomendas) que não possui carro nem mota e vive numa zona com poucos transportes públicos. Depende do serviço contínuo de bicicletas elétricas da Mober para trabalhar turnos de 8 horas ou mais sem interrupções, procurando rapidez, menor esforço físico e segurança nas paragens.

* **Persona 2 — Operador / Técnico de Terreno (Field Technician): Carlos Mendes (34 anos)**
  Funcionário da Mober que anda na rua (com carrinha de apoio). Usa o seu **dashboard operacional** para ver que bicicletas estão com problemas (`Maintenance`), receber e resolver os tickets de avaria nos hubs dentro do SLA, trocar baterias e mover bicicletas entre hubs para garantir que os estafetas encontram veículos carregados (>= 60%) e vagas livres.

* **Persona 3 — Administrador da Mober (Service & Platform Admin): Equipa Gestora Mober**
  Responsável pela gestão administrativa e técnica da empresa Mober. Tem acesso completo ao painel de administração para registar novos hubs e veículos, retirar bicicletas irreparáveis (`Retired`), atribuir tickets aos operadores de terreno, gerir contas e permissões (*roles*) de utilizadores/funcionários, configurar preços/cauções e monitorizar a saúde da plataforma.

---

## 2. User Stories — Todas Juntas

### A. User Stories do Estafeta (`US-EST-*`)

| ID | História (Como / Quero / Para) | Critérios de Aceitação |
| :--- | :--- | :--- |
| **US-EST-01** | **Como** estafeta, **quero** ver no mapa os hubs próximos com bicicletas carregadas (>= 60%) e a respetiva bateria, **para** escolher um hub com autonomia garantida para o meu turno. | • Apenas bicicletas acima do mínimo operacional (>= 60%) aparecem disponíveis para reserva.<br>• Hubs mostram contagem de veículos disponíveis e % de bateria. |
| **US-EST-02** | **Como** estafeta, **quero** reservar 1 bicicleta num hub por um máximo de 30 minutos, **para** garantir que o veículo está disponível quando chegar ao posto. | • Máximo 1 reserva normal ativa por utilizador.<br>• Reserva expira automaticamente após 30 minutos sem levantamento. |
| **US-EST-03** | **Como** estafeta, **quero** autorizar a caução na reserva e pagar no final da utilização consoante a distância percorrida (`distância * preço/km`), consultando o resumo de utilizações e cobranças, **para** gerir os meus custos de trabalho de forma transparente. | • Retenção de caução via payment sandbox.<br>• Cálculo no final da utilização ao parar num hub (`distância percorrida * preço/km`), débito na conta do utilizador e registo no dashboard. |
| **US-EST-04** | **Como** estafeta, **quero** ativar e desativar o bloqueio temporário (motor e rodas) na aplicação durante paragens curtas, **para** recolher e entregar pedidos em segurança sem terminar o meu serviço. | • Estado alterna entre `In Use` e bloqueio temporário (`Paused/Locked`).<br>• Motor e rodas imobilizados; retoma imediata sem nova reserva. |
| **US-EST-05** | **Como** estafeta em viagem, **quero** receber um aviso de bateria baixa (10%) e estar protegido pelo corte automático do motor aos 5%, **para** planear a troca antes de ficar sem comunicação ou GPS. | • Telemetria dispara notificação ao atingir o limiar de aviso (10%).<br>• Aos 5% de bateria, o motor elétrico é desligado automaticamente para preservar GPS e comunicação. |
| **US-EST-06** | **Como** estafeta, quando a bateria da bicicleta que estou a usar passa abaixo do limiar crítico (10%, ou seja, quando dá a notificação), **quero** poder saber qual o hub perto de mim com uma bicicleta com bateria >= 60% disponível para utilizar, **para** efetuar a troca rapidamente sem ficar apeado. | • Ao atingir o limiar crítico (10%), apresenta os hubs próximos com bicicletas `Available` e bateria >= 60%.<br>• Mantém o limite de 1 reserva ativa em simultâneo. |
| **US-EST-07** | **Como** estafeta, **quero** saber quais os hubs próximos com vagas disponíveis quando pretendo devolver a bicicleta, **para** não me deslocar a um posto cheio no final do turno. | • Mostra apenas hubs com lotação < 100% para devolução.<br>• Impede encerrar viagem num hub lotado. |
| **US-EST-08** | **Como** estafeta, **quero** reportar uma avaria abrindo um ticket e bloquear a bicicleta ao devolvê-la no hub, **para** poder trocar de bicicleta e impedir que outros utilizadores a escolham. | • Report obriga a selecionar/descrever motivo.<br>• Ao entregar no hub, veículo passa imediatamente a `Maintenance` (indisponível para reserva) e gera ticket de SLA. |

---

### B. User Stories do Operador de Terreno (`US-OP-*`)

| ID | História (Como / Quero / Para) | Critérios de Aceitação |
| :--- | :--- | :--- |
| **US-OP-01** | **Como** operador de terreno, **quero** consultar no meu dashboard as bicicletas com problemas (`Maintenance`), os tickets em aberto e o estado dos hubs, podendo selecionar e assumir os tickets que vou resolver, **para** organizar a minha rota de intervenção na cidade. | • Dashboard mostra bicicletas em manutenção, lista de tickets em aberto (por assumir) e os já assumidos por mim, com localização do hub e prazo restante do SLA.<br>• Operador seleciona/assume manualmente um ou vários tickets disponíveis, ficando associados a si.<br>• Destaca hubs com falta de bicicletas carregadas (>= 60%) ou lotados. |
| **US-OP-02** | **Como** operador de terreno, **quero** registar a reparação de uma bicicleta e fechar o respetivo ticket de manutenção que assumi, **para** a voltar a disponibilizar (`Available`) para os estafetas dentro do SLA. | • Permite registar notas de reparação e fechar o ticket assumido.<br>• Ao fechar o ticket, o estado da bicicleta volta a `Available` (ou solicita retirada se irreparável). |
| **US-OP-03** | **Como** operador de terreno, **quero** registar a movimentação de bicicletas entre hubs e atualizações de bateria após troca no local, **para** manter a rede equilibrada antes das horas de ponta. | • Permite atualizar o hub associado a uma bicicleta durante o rebalanceamento.<br>• Reflete imediatamente a nova disponibilidade nos hubs. |

---

### C. User Stories do Administrador da Mober (`US-ADM-*`)

| ID | História (Como / Quero / Para) | Critérios de Aceitação |
| :--- | :--- | :--- |
| **US-ADM-01** | **Como** administrador da Mober, **quero** registar novos hubs e novos veículos no sistema, bem como ativar ou retirar definitivamente bicicletas (`Retired`), **para** gerir o inventário total da frota. | • Permite criar hub (nome, localização, capacidade) e bicicleta (ID, hub inicial).<br>• Permite desativar temporariamente ou passar permanentemente para `Retired` (nunca mais reservável). |
| **US-ADM-02** | **Como** administrador da Mober, **quero** supervisionar todos os tickets abertos (por reportes ou telemetria) e gerir exceções urgentes, **para** garantir que nenhum ticket fica esquecido e os SLAs são cumpridos. | • Mostra todos os tickets (em aberto, assumidos e fechados) e alertas visuais para tickets em risco de violar o SLA.<br>• Permite criar ticket manual ou atribuir em casos excecionais/urgentes. |
| **US-ADM-03** | **Como** administrador da Mober, **quero** gerir contas de utilizadores, funcionários e respetivas permissões (*roles*), **para** controlar quem acede às funcionalidades de estafeta, operador de terreno, cliente ou admin. | • Permite criar contas de operadores/técnicos e gerir *roles* (RBAC).<br>• Permite suspender/bloquear contas em caso de incumprimento. |
| **US-ADM-04** | **Como** administrador da Mober, **quero** configurar os valores de caução e o preço por quilómetro, **para** manter o modelo comercial da empresa atualizado. | • Permite definir valor da caução e preço por km.<br>• Novas sessões/reservas aplicam os valores configurados. |
| **US-ADM-05** | **Como** administrador da Mober, **quero** monitorizar num painel global a disponibilidade da frota e a saúde técnica da plataforma, **para** detetar falhas de sistema ou de telemetria rapidamente. | • Painel apresenta contagens globais de veículos por estado, estado dos serviços/telemetria e taxa de resolução de tickets. |
