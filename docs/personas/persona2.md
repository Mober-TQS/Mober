# Persona 2 — Operador / Técnico de Terreno (Field Technician)

## 1. Perfil da Persona

* **Nome:** Carlos Mendes
* **Idade:** 34 anos
* **Ocupação:** Operador de Terreno e Técnico de Manutenção da Mober
* **Perfil Tecnológico:** Usa o tablet/telemóvel na carrinha de apoio enquanto circula pela cidade; precisa de um dashboard operacional direto que mostre o mapa de hubs, bicicletas com problemas (`Maintenance`) e a lista de tickets em aberto por zona.
* **Contexto e Motivação:**
  O Carlos é o funcionário de terreno da Mober responsável por manter os hubs abastecidos e as bicicletas operacionais na rua. Como os estafetas dependem de encontrar bicicletas carregadas (>= 60%) para trocas rápidas ao longo do dia, o Carlos anda no terreno a trocar baterias, mover bicicletas de hubs cheios para hubs vazios e recolher/reparar bicicletas com problemas (`Maintenance`). Ele próprio escolhe no dashboard os tickets em aberto que ficam na sua rota (por exemplo, um lote de 4 ou 5 tickets próximos) para otimizar as deslocações e cumprir os prazos de SLA (24 a 48 horas).
* **Objetivos:**
  * Consultar rapidamente que hubs têm falta de bicicletas carregadas (>= 60%) ou estão quase cheios.
  * Selecionar e assumir tickets de manutenção em aberto na sua zona para organizar a rota diária da carrinha.
  * Reparar as bicicletas avariadas dentro do SLA (24 a 48 horas) e colocá-las novamente como `Available`.
* **Frustrações / Dores:**
  * Deslocar-se a um hub sem saber quantas bicicletas estão em `Maintenance` naquela zona.
  * Outro técnico ir ao mesmo hub tratar do mesmo problema por falta de marcação de quem assumiu o ticket.
  * Encontrar uma bicicleta no hub cujo dano é demasiado grave para reparar na rua ou na oficina.

---

## 2. Cenário Principal (Happy Path)

**Contexto:** Turno diário do Carlos no terreno com a carrinha de apoio da Mober, das **08:00 às 17:00**.

1. **Início do Turno e Escolha de Tickets (08:00):**
   Antes de sair com a carrinha, o Carlos abre o **Dashboard do Operador de Terreno**. Consulta a lista de tickets de manutenção em aberto (reportados por estafetas ou pela telemetria) com a localização do hub e o tempo restante de SLA. Vê que existem vários tickets na zona central da cidade, **seleciona 5 tickets dessa zona e assume-os** (ficando atribuídos a si como `In Progress`, para que mais nenhum colega vá aos mesmos locais).

2. **Rebalanceamento e Troca de Baterias Antes da Hora de Ponta (10:30):**
   No dashboard, o Carlos vê um alerta de que o *Hub Praça dos Restaurantes* tem poucas bicicletas com carga >= 60% e que outro hub residencial periférico está quase a 100% da capacidade. Antes do pico de almoço dos estafetas, passa pelo hub periférico, carrega bicicletas na carrinha, coloca-as no hub dos restaurantes com baterias carregadas e atualiza a movimentação no sistema.

3. **Intervenção nos Hubs e Reparação de Bicicletas (14:00):**
   O Carlos segue a sua rota pelos 5 tickets que assumiu. Ao chegar ao hub onde o estafeta André deixou a bicicleta com falha nos travões (em estado `Maintenance`), afina e substitui os calços de travão (ou faz a reparação na oficina da Mober).

4. **Fecho do Ticket e Reativação da Bicicleta (16:15):**
   Concluída a reparação e testada a segurança da bicicleta, o Carlos abre o ticket no seu dashboard, regista as notas da reparação e **fecha o ticket** dentro do prazo de SLA (24 a 48 horas). O sistema passa automaticamente a bicicleta do estado `Maintenance` para **`Available`**, ficando imediatamente visível para os estafetas.

---

## 3. Variantes e Cenários Extra (Exceções)

### Cenário Extra 1 — Bicicleta Irreparável no Terreno / Oficina
* **Contexto:** Ao inspecionar uma das bicicletas dos 5 tickets que assumiu, o Carlos percebe que o motor elétrico queimou ou o quadro está partido, não tendo reparação possível.
* **Comportamento:** O Carlos regista no ticket que a bicicleta não tem reparação viável e sinaliza-a para abate/retirada definitiva (`Retired` pelo Administrador), garantindo que não volta para o estado `Available`.

### Cenário Extra 2 — Prioridade a Ticket com SLA Prestes a Expirar
* **Contexto:** Ao abrir o dashboard, o Carlos vê que um ticket de avaria aberto no dia anterior está a poucas horas de atingir o limite do SLA (24 a 48 horas).
* **Comportamento:** Como o dashboard destaca visualmente os tickets com menor tempo restante de SLA no topo da lista, o Carlos assume esse ticket em primeiro lugar e inicia a rota por esse hub.

---

## 4. User Stories (`US-OP-*`)

| ID | Origem no Cenário | User Story (Como / Quero / Para) | Critérios de Aceitação |
| :--- | :--- | :--- | :--- |
| **US-OP-01** | Passo 1 & Extra 2 | **Como** operador de terreno, **quero** consultar no meu dashboard as bicicletas com problemas (`Maintenance`), os tickets em aberto e o estado dos hubs, podendo selecionar e assumir os tickets que vou resolver, **para** organizar a minha rota de intervenção na cidade. | • Dashboard mostra bicicletas em manutenção, lista de tickets em aberto (por assumir) e os já assumidos por mim, com localização do hub e prazo restante do SLA.<br>• Operador seleciona/assume manualmente um ou vários tickets disponíveis, ficando associados a si.<br>• Destaca hubs com falta de bicicletas carregadas (>= 60%) ou lotados. |
| **US-OP-02** | Passo 3, 4 & Extra 1 | **Como** operador de terreno, **quero** registar a reparação de uma bicicleta e fechar o respetivo ticket de manutenção que assumi, **para** a voltar a disponibilizar (`Available`) para os estafetas dentro do SLA. | • Permite registar notas de reparação e fechar o ticket assumido.<br>• Ao fechar o ticket, o estado da bicicleta volta a `Available` (ou solicita retirada se irreparável). |
| **US-OP-03** | Passo 2 | **Como** operador de terreno, **quero** registar a movimentação de bicicletas entre hubs e atualizações de bateria após troca no local, **para** manter a rede equilibrada antes das horas de ponta. | • Permite atualizar o hub associado a uma bicicleta durante o rebalanceamento.<br>• Reflete imediatamente a nova disponibilidade nos hubs. |
