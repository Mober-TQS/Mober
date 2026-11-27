# Persona 3 — Administrador da Mober (Service & Platform Admin)

## 1. Perfil da Persona

* **Nome:** Equipa Gestora / Administrador Mober (ex.: Sofia Martins)
* **Idade:** 29 anos
* **Ocupação:** Administradora da Plataforma e Gestora de Operações da Mober
* **Perfil Tecnológico:** Utilizadora avançada de backoffice web em computador; gere a configuração técnica, o inventário total da frota, as contas de utilizadores/funcionários e os indicadores globais do negócio.
* **Contexto e Motivação:**
  Como a Mober é a empresa proprietária da plataforma e da frota, a equipa de administração precisa de um backoffice central para gerir tudo o que está acima da operação diária de rua: expandir a rede (registar novos hubs e novas bicicletas), retirar veículos em fim de vida (`Retired`), criar contas e permissões (*roles*) para novos operadores de terreno, configurar os preços/cauções do serviço contínuo e supervisionar a saúde global da plataforma, da telemetria e do cumprimento dos SLAs.
* **Objetivos:**
  * Gerir o catálogo completo de hubs e veículos ao longo de todo o seu ciclo de vida (`Available`, `Reserved`, `In Use`, `Maintenance`, `Retired`).
  * Supervisionar se os tickets de manutenção estão a ser assumidos e resolvidos dentro do SLA (24 a 48 horas).
  * Controlar acessos, contas de funcionários e regras de negócio/preços (caução, turno contínuo, *overtime*).
  * Monitorizar métricas globais de utilização e a estabilidade técnica da plataforma (API e telemetria).
* **Frustrações / Dores:**
  * Tickets urgentes ficarem esquecidos sem nenhum operador de terreno os assumir a tempo do SLA.
  * Bicicletas abatidas ou irreparáveis continuarem a contar como inventário ativo por falta de estado `Retired`.
  * Falhas silenciosas na receção de telemetria ou picos de erro na API durante as horas de ponta dos estafetas.

---

## 2. Cenário Principal (Happy Path)

**Contexto:** Gestão diária e supervisão da plataforma no Backoffice de Administração da Mober.

1. **Supervisão Matinal da Frota e Saúde do Sistema (09:00):**
   A Administradora acede ao **Painel de Administração da Mober**. No dashboard global, verifica a distribuição total das bicicletas por estado (`Available`, `Reserved`, `In Use`, `Maintenance`, `Retired`), confirma que o fluxo de mensagens do simulador de telemetria e a latência da API estão normais e consulta a taxa de resolução de tickets dentro do SLA.

2. **Expansão da Rede — Registo de Novo Hub e Veículos (10:15):**
   A Mober instalou uma nova estação junto a um centro comercial e adquiriu 5 novas bicicletas elétricas. A Administradora entra na área de inventário, **cria o novo hub** (indicando nome, coordenadas e capacidade máxima de docas) e **regista as 5 bicicletas novas**, associando-as ao hub e ativando-as no estado `Available`.

3. **Gestão de Contas e Permissões — Novo Operador de Terreno (11:30):**
   Entrou um novo técnico de terreno para a equipa da Mober. A Administradora acede à gestão de utilizadores, cria/valida a conta do funcionário e atribui-lhe a permissão (*role*) de **Operador de Terreno**, permitindo-lhe aceder ao dashboard operacional de manutenção.

4. **Supervisão de Tickets e Abate de Veículo Irreparável (`Retired`) (15:00):**
   Durante a tarde, a Administradora revê os tickets de manutenção. Vê que o operador Carlos sinalizou uma bicicleta cujo quadro partiu e não tem arranjo. A Administradora abre a ficha desse veículo e altera o seu estado definitivamente para **`Retired`**, retirando-o de circulação para que nunca mais possa ser reservado.

5. **Ajuste de Tarifas e Parâmetros do Serviço (17:00):**
   Para ajustar a operação, acede às configurações comerciais e atualiza os parâmetros do serviço (como o valor da caução, tarifa de turno contínuo e taxa de *overtime* ou limites operacionais), que passam a ser aplicados automaticamente às novas sessões dos estafetas.

---

## 3. Variantes e Cenários Extra (Exceções)

### Cenário Extra 1 — Ticket Urgente Sem Operador (Risco de SLA)
* **Contexto:** Um ticket de manutenção está próximo do limite do SLA (24 a 48 horas), mas ainda permanece em aberto porque todos os operadores de terreno estavam ocupados noutras zonas.
* **Comportamento:** O painel de administração emite um alerta de **SLA em risco**. A Administradora intervém diretamente e atribui/encaminha o ticket com prioridade máxima a um operador disponível para garantir que o prazo não falha.

### Cenário Extra 2 — Suspensão de Conta por Incumprimento Grave
* **Contexto:** Um utilizador acumula múltiplos abandonos de bicicletas fora dos hubs ou fraudes de pagamento.
* **Comportamento:** Na gestão de utilizadores, a Administradora suspende/bloqueia a conta desse utilizador, impedindo-o de iniciar novas reservas até a situação estar regularizada.

### Cenário Extra 3 — Alerta de Degradação Técnica / Telemetria
* **Contexto:** O atraso (*lag*) das mensagens de telemetria sobe acima do limite esperado ou um hub deixa de comunicar.
* **Comportamento:** O painel de saúde técnica destaca a anomalia nas métricas de observabilidade, permitindo à equipa técnica da Mober reiniciar ou corrigir o adapter/simulador antes que afete os alertas de bateria dos estafetas.

---

## 4. User Stories (`US-ADM-*`)

| ID | Origem no Cenário | User Story (Como / Quero / Para) | Critérios de Aceitação |
| :--- | :--- | :--- | :--- |
| **US-ADM-01** | Passo 2 & 4 | **Como** administrador da Mober, **quero** registar novos hubs e novos veículos no sistema, bem como ativar ou retirar definitivamente bicicletas (`Retired`), **para** gerir o inventário total da frota. | • Permite criar hub (nome, localização, capacidade) e bicicleta (ID, hub inicial, bateria).<br>• Permite desativar temporariamente ou passar permanentemente para `Retired` (nunca mais reservável). |
| **US-ADM-02** | Passo 4 & Extra 1 | **Como** administrador da Mober, **quero** supervisionar todos os tickets abertos (por reportes ou telemetria) e gerir exceções urgentes, **para** garantir que nenhum ticket fica esquecido e os SLAs são cumpridos. | • Mostra todos os tickets (em aberto, assumidos e fechados) e alertas visuais para tickets em risco de violar o SLA.<br>• Permite criar ticket manual ou atribuir em casos excecionais/urgentes. |
| **US-ADM-03** | Passo 3 & Extra 2 | **Como** administrador da Mober, **quero** gerir contas de utilizadores, funcionários e respetivas permissões (*roles*), **para** controlar quem acede às funcionalidades de estafeta, operador de terreno ou admin. | • Permite criar contas de operadores/técnicos e gerir *roles* (RBAC).<br>• Permite suspender/bloquear contas em caso de incumprimento. |
| **US-ADM-04** | Passo 5 | **Como** administrador da Mober, **quero** configurar os valores de caução, tarifas de serviço contínuo, taxas de *overtime* e regras operacionais, **para** manter o modelo comercial e operacional da empresa atualizado. | • Permite definir valor da caução, preço do serviço e parâmetros operacionais.<br>• Novas sessões/reservas aplicam os valores configurados. |
| **US-ADM-05** | Passo 1 & Extra 3 | **Como** administrador da Mober, **quero** monitorizar num painel global a disponibilidade da frota, KPIs e a saúde técnica da plataforma, **para** detetar falhas de sistema ou de telemetria rapidamente. | • Painel apresenta contagens globais de veículos por estado, taxa de resolução de tickets e métricas técnicas (latência da API, taxa de erro, *lag* de telemetria). |
