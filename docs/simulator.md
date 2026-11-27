# Documentação do Fleet Simulator — Möber


### 2.1. Orquestração com Docker Compose (`docker-compose.yml`)
* Adicionado o ficheiro `docker-compose.yml` na raiz do projeto Möber.
* Orquestra simultaneamente:
  * **`broker`**: Contentor `eclipse-mosquitto:2.0.22` na porta `1883` sem autenticação para facilidade de desenvolvimento local e pipelines.
  * **`simulator`**: Imagem construída a partir do diretório `./simulator`, parametrizada via variáveis de ambiente (`SIMULATOR_MQTT_HOST=broker`, `SIMULATOR_MQTT_PORT=1883`, `SIMULATOR_SCENARIO_FILE=classpath:scenarios/mober.yml`).
* Permite colocar todo o ecossistema de simulação a correr com um simples comando: `docker compose up -d`.

### 2.2. Configuração por Omissão (`application.properties`)
* A configuração padrão do Spring Boot em `simulator/src/main/resources/application.properties` foi configurada para carregar automaticamente o cenário `classpath:scenarios/mober.yml`, garantindo que o simulador arranca imediatamente com a frota Möber.

### 2.3. Criação do Cenário Principal Möber (`scenarios/mober.yml`)
Substituiu-se a frota genérica de demonstração por uma frota de entregas personalizada:
* **Identificador da frota:** `fleetId: mober-fleet-01`.
* **Hubs reais de Aveiro:** Definição de 6 postos estratégicos de estafetas com coordenadas GPS WGS84 reais:
  1. `hub-glicinias`: Centro Comercial Glicínias Plaza (`lat: 40.6272`, `lon: -8.6471`)
  2. `hub-forum`: Fórum Aveiro (`lat: 40.6412`, `lon: -8.6501`)
  3. `hub-estacao`: Estação Ferroviária de Aveiro (`lat: 40.6443`, `lon: -8.6455`)
  4. `hub-universidade`: Universidade de Aveiro — Campus Santiago (`lat: 40.6304`, `lon: -8.6577`)
  5. `hub-hospital`: Hospital Infante D. Pedro (`lat: 40.6358`, `lon: -8.6492`)
  6. `hub-cais`: Cais da Fonte Nova (`lat: 40.6392`, `lon: -8.6442`)
* **Frota Möber (20 Bicicletas Elétricas):**
  * Todas do tipo `E_BIKE` com identificadores padronizados `mober-bike-0001` até `mober-bike-0020`.
  * Níveis de bateria distribuídos estrategicamente para validar as regras de negócio dos Épicos Möber:
    * **$\ge 70\%$** (ex: 100%, 95%, 92%, 88%, etc.): aptas para reserva prévia e início de turno de trabalho contínuo (requisito de negócio de autonomia).
    * **$\approx 25\%$** (`mober-bike-0011` com 25%): verificação do limiar de alerta preventivo de bateria fraca.
    * **$< 20\%$** (`mober-bike-0004` com 18%, `mober-bike-0008` com 15%, `mober-bike-0020` com 19%): limiar que desencadeia a funcionalidade de *battery swap* e reserva simultânea de veículo de substituição.
    * **$\le 10\%$** (`mober-bike-0012` com 9%): disparo do alerta crítico de recolha obrigatória ao estafeta.
    * **$\le 5\%$** (`mober-bike-0016` com 5%): teste da regra de proteção de corte de tração do motor elétrico para preservação de telemetria/GPS.
* **Avarias Pré-Programadas (Scripted Faults):**
  * `mober-bike-0004`: Falha de travões (`BRAKE_WEAR`) injetada aos **40 segundos**, com resolução/reparação automática após **120 segundos** (ou seja, aos **160 segundos** do simulador).
  * `mober-bike-0016`: Sobreaquecimento crítico de bateria (`BATTERY_OVERHEAT`) injetado aos **80 segundos**, mantendo-se ativa para testar ordens de manutenção permanentes.

### 2.4. Criação do Cenário Dinâmico de Viagens (`scenarios/mober-trips.yml`)
* Adicionado cenário para testar viagens ativas e movimento contínuo dos estafetas:
  * `tripProbability: 0.05`: probabilidade de 5% por ciclo de uma bicicleta livre iniciar entrega.
  * `rideSpeedKmh: 18`: velocidade de deslocação realista de estafetas urbanos ($18\text{ km/h}$).
  * `drainPercentPerMinute: 0.5`: taxa de consumo de bateria em andamento ($0.5\%$ por minuto).
  * `chargePercentPerMinute: 0.6`: taxa de carregamento quando estacionada e trancada num hub ($0.6\%$ por minuto).

### 2.5. Atualização de Exemplos e Documentação
* Ficheiro de exemplo `simulator/examples/scenario.yml` revisto para alinhar com a nomenclatura `mober-fleet-01`, postos de Aveiro e identificadores `mober-bike-0001` / `mober-bike-0002`.
* Requisitos formais e épicos do projeto organizados na pasta `docs/`.

---

## 3. O Que o Simulador Está a Gerar e em Que Tempos

O simulador trabalha com base num relógio interno síncrono (*tick engine*). Abaixo detalham-se os tempos, as taxas de geração, os tópicos e os formatos de cada fluxo.

### 3.1. Cadência Temporal e Taxa de Emissão (Timings)

| Parâmetro | Valor Configurado | Significado Operacional |
| :--- | :--- | :--- |
| **Intervalo de Emissão por Bicicleta** | **`10 segundos`** | Cada uma das 20 bicicletas emite uma leitura de telemetria a cada 10 s. |
| **Primeira Emissão** | **$t = 0\text{ s}$** | Ocorre imediatamente no momento de arranque da aplicação. |
| **Ordem dos Veículos** | Sequencial por tick | As 20 bicicletas são processadas e emitidas sequencialmente no mesmo tick de relógio. |
| **Taxa Global de Mensagens (Frota Padrão)** | **`2.0 mensagens/segundo`** | $\frac{20\text{ veículos}}{10\text{ segundos}} = 2.0\text{ msg/s}$ ($120\text{ msg/minuto}$). |
| **Taxa em Cenário de Carga (`load-test.yml`)** | **`1000.0 mensagens/segundo`** | 1000 veículos gerados com intervalo de 1 s para testes de stress/SLO. |
| **Relatórios de Débito no Log** | **A cada `10 segundos`** | O simulador afixa a taxa efetiva medida vs taxa-alvo no stdout/logs. |

---

### 3.2. Mensagens de Telemetria (`telemetry`)

* **Tópico:** `fleet/mober-fleet-01/vehicles/{vehicleId}/telemetry`
* **QoS:** `1` (At least once).
* **Retained:** **Sim (`true`)**. O broker guarda a última leitura conhecida de cada bicicleta, permitindo que o backend ou frontend recebam imediatamente o estado da frota ao ligarem-se.
* **Frequência:** Emitida a cada **10 segundos** para cada bicicleta.

#### Estrutura do Payload:
```json
{
  "schemaVersion": "1.0.0",
  "messageId": "7d9f2c4e-3b1a-4e55-9a60-0c1f8f6b1a01",
  "type": "telemetry",
  "timestamp": "2026-11-27T17:35:10Z",
  "fleetId": "mober-fleet-01",
  "vehicleId": "mober-bike-0001",
  "data": {
    "batteryPercent": 95.0,
    "lockState": "locked",
    "position": {
      "lat": 40.6272,
      "lon": -8.6471
    },
    "hubId": "hub-glicinias",
    "faultCode": null,
    "speedKmh": 0.0
  }
}
```

#### Variação dos Campos ao Longo do Tempo:
1. **Estado Trancada no Hub (`locked`):**
   * `lockState`: `"locked"`
   * `hubId`: Identificador do posto (ex: `"hub-glicinias"`).
   * `position`: Coordenadas exatas do hub.
   * `speedKmh`: `0.0`
   * `batteryPercent`: Carrega $+0.1\%$ a cada tick de 10 s (à razão de $+0.6\%/\text{minuto}$), até atingir 100.0%.
2. **Estado em Viagem / Deslocação (`unlocked`):**
   * `lockState`: `"unlocked"`
   * `hubId`: `null` (não está em nenhum hub durante a viagem).
   * `position`: Coordenadas GPS interpoladas ao longo da rota em linha reta entre o hub de partida e o de destino.
   * `speedKmh`: `18.0` km/h.
   * `batteryPercent`: Descarrega $-0.0833\%$ a cada tick de 10 s (à razão de $-0.5\%/\text{minuto}$).
3. **Estado em Pausa no Hub (`unlocked` aguardando tranca):**
   * `lockState`: `"unlocked"`
   * `hubId`: Identificador do hub onde terminou a rota.
   * `speedKmh`: `0.0`
   * `batteryPercent`: Estável (não carrega nem descarrega até ser trancada).

---

### 3.3. Eventos de Avaria e Trancamento (`events`)

* **Tópico:** `fleet/mober-fleet-01/vehicles/{vehicleId}/events`
* **QoS:** `1`
* **Retained:** **Não (`false`)** (são eventos efémeros disparados na transição de estado).
* **Frequência:** Não periódicos; gerados apenas no momento preciso da transição.

#### Cronologia de Eventos Pré-Programados (`mober.yml`):

| Instante Temporal | Veículo | Tipo de Evento (`eventType`) | Código (`faultCode`) | Severidade | Descrição / Efeito |
| :---: | :---: | :---: | :---: | :---: | :--- |
| **$t = 40\text{ s}$** | `mober-bike-0004` | `FAULT_RAISED` | `BRAKE_WEAR` | `WARNING` | Desgaste crítico de travões detetado. O veículo deixa de permitir desbloqueios. |
| **$t = 80\text{ s}$** | `mober-bike-0016` | `FAULT_RAISED` | `BATTERY_OVERHEAT` | `CRITICAL` | Sobreaquecimento de bateria gerado. Permanece em alerta contínuo. |
| **$t = 160\text{ s}$** | `mober-bike-0004` | `FAULT_CLEARED` | `BRAKE_WEAR` | `WARNING` | Resolução da avaria (40 s + 120 s). O veículo volta a estar apto para serviço. |

#### Exemplo de Payload de Evento:
```json
{
  "schemaVersion": "1.0.0",
  "messageId": "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d",
  "type": "event",
  "timestamp": "2026-11-27T17:35:40Z",
  "fleetId": "mober-fleet-01",
  "vehicleId": "mober-bike-0004",
  "data": {
    "eventType": "FAULT_RAISED",
    "severity": "WARNING",
    "faultCode": "BRAKE_WEAR",
    "lockState": null,
    "description": "Brake wear detected on vehicle mober-bike-0004"
  }
}
```

---

### 3.4. Comandos e Respostas em Tempo Real (`commands` e `responses`)

O backend do Möber pode controlar a frota simulada enviando mensagens nos tópicos de comando:

* **Tópicos de Comando:**
  * `fleet/mober-fleet-01/vehicles/{vehicleId}/commands/unlock`
  * `fleet/mober-fleet-01/vehicles/{vehicleId}/commands/lock`
  * `fleet/mober-fleet-01/vehicles/{vehicleId}/commands/ping`
* **Tópico de Resposta:** `fleet/mober-fleet-01/vehicles/{vehicleId}/responses`
* **Tempo de Resposta:** Imediato ($< 50\text{ ms}$), processado de forma síncrona na receção MQTT.

#### Resumo de Comportamento dos Comandos:

| Comando | Estado Estacionada (`Parked`) | Estado em Viagem (`Riding`) | Estado Aguardando no Hub |
| :--- | :--- | :--- | :--- |
| **`unlock`** | Inicia viagem para outro hub. Se tiver avaria ativa (`faultCode`), responde `REJECTED (VEHICLE_FAULT)`. | Responde `COMPLETED` (idempotente). | Responde `COMPLETED` (mantém-se destrancada). |
| **`lock`** | Responde `COMPLETED` (já estava trancada). | Responde `REJECTED (NOT_AT_HUB)`. Não é permitido trancar a meio de uma viagem. | Tranca a bicicleta no novo hub; responde `COMPLETED`. |
| **`ping`** | Responde `COMPLETED`. | Responde `COMPLETED`. | Responde `COMPLETED`. |

#### Exemplo de Resposta do Simulador:
```json
{
  "schemaVersion": "1.0.0",
  "messageId": "9f8e7d6c-5b4a-3210-fedc-ba9876543210",
  "type": "response",
  "timestamp": "2026-11-27T17:36:00Z",
  "fleetId": "mober-fleet-01",
  "vehicleId": "mober-bike-0001",
  "data": {
    "commandId": "e5b2a9c1-0d3f-4a8e-8b77-1c2d3e4f5a04",
    "status": "COMPLETED"
  }
}
```

---

## 4. Guia Rápido de Utilização e Validação

### 4.1. Iniciar o Simulador e Broker
```bash
# Na raiz do repositório M-ber
docker compose up -d
```

### 4.2. Monitorizar Toda a Telemetria da Frota Möber
```bash
docker compose exec broker mosquitto_sub -v -t 'fleet/mober-fleet-01/vehicles/+/telemetry'
```

### 4.3. Monitorizar Alertas e Avarias
```bash
docker compose exec broker mosquitto_sub -v -t 'fleet/mober-fleet-01/vehicles/+/events'
```

### 4.4. Testar Desbloqueio de um Veículo
```bash
docker compose exec broker mosquitto_pub -t 'fleet/mober-fleet-01/vehicles/mober-bike-0001/commands/unlock' -m '{
  "schemaVersion": "1.0.0",
  "messageId": "c3a1f8d0-6a24-4f67-b1d2-9e3e4a5b6c03",
  "type": "command",
  "timestamp": "2026-11-27T17:40:00Z",
  "fleetId": "mober-fleet-01",
  "vehicleId": "mober-bike-0001",
  "data": {
    "commandId": "e5b2a9c1-0d3f-4a8e-8b77-1c2d3e4f5a04"
  }
}'
```
Ao enviar este comando, o simulador emite uma resposta de confirmação em `fleet/mober-fleet-01/vehicles/mober-bike-0001/responses` e a bicicleta inicia a viagem para outro hub, passando o `lockState` para `unlocked` e reportando coordenadas intermediárias nos ticks seguintes (a cada 10 s).
