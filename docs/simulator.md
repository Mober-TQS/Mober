# Simulador de Telemetria da Frota — Möber

Serviço de simulação da frota de bicicletas elétricas para o projeto **Möber**, baseado no simulador de telemetria sobre MQTT.

---

## 1. Como Correr

O simulador e o broker MQTT correm através do Docker Compose:

```bash
# Iniciar o broker e o simulador em segundo plano
docker compose up -d

# Ver os logs do simulador
docker compose logs -f simulator

# Parar os serviços
docker compose down
```

* **Broker MQTT:** Acessível em `localhost:1883` (sem autenticação).
* **Cenário carregado:** `scenarios/mober.yml` (configurado por omissão em `application.properties`).

---

## 2. Cenário Möber (`mober.yml`)

O cenário modela a operação em **Aveiro**, com:
* **Identificador da frota:** `mober-fleet-01`.
* **6 Hubs estratégicos:** Glicínias Plaza, Fórum Aveiro, Estação, Universidade de Aveiro, Hospital e Cais da Fonte Nova.
* **20 Bicicletas Elétricas (`mober-bike-0001` a `mober-bike-0020`):**
  * Níveis de bateria configurados para validar regras de negócio (>70% para início de turno, ~25% alerta preventivo, <20% troca de bateria/swap, <10% crítico e 5% corte de tração).
* **Avarias programadas:**
  * `mober-bike-0004`: Desgaste de travões (`BRAKE_WEAR`) aos 40s (limpa automaticamente aos 160s).
  * `mober-bike-0016`: Sobreaquecimento (`BATTERY_OVERHEAT`) aos 80s (permanente).

---

## 3. Tópicos MQTT e Formato de Mensagens

Todas as mensagens seguem a especificação JSON Schema v1.0.0.

### 3.1. Telemetria periódica
* **Tópico:** `fleet/mober-fleet-01/vehicles/{vehicleId}/telemetry`
* **Frequência:** A cada 10 segundos por veículo (com flag *retained* ativa).
* **Exemplo de Payload:**
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
    "position": { "lat": 40.6272, "lon": -8.6471 },
    "hubId": "hub-glicinias",
    "faultCode": null,
    "speedKmh": 0.0
  }
}
```

### 3.2. Eventos e Avarias
* **Tópico:** `fleet/mober-fleet-01/vehicles/{vehicleId}/events`
* **Emitido:** Apenas quando ocorre uma alteração de estado (ex: `FAULT_RAISED`, `FAULT_CLEARED`, `LOCK_CHANGED`).
* **Exemplo de Payload:**
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

### 3.3. Comandos e Respostas
* **Tópicos para enviar comandos:** `fleet/mober-fleet-01/vehicles/{vehicleId}/commands/{unlock|lock|ping}`
* **Tópico de resposta:** `fleet/mober-fleet-01/vehicles/{vehicleId}/responses`
* **Comportamento:**
  * `unlock`: Destranca a bicicleta e inicia viagem para outro hub (rejeitado se tiver avaria ativa).
  * `lock`: Tranca a bicicleta no hub (rejeitado se estiver a meio de uma viagem).
  * `ping`: Verifica a conectividade do veículo (responde `COMPLETED`).

---

## 4. Testes e Validação Rápida

Com os contentores a correr, pode validar-se o funcionamento com o cliente `mosquitto`:

```bash
# 1. Ouvir a telemetria de todas as bicicletas
docker compose exec broker mosquitto_sub -v -t 'fleet/mober-fleet-01/vehicles/+/telemetry'

# 2. Ouvir apenas avarias e eventos
docker compose exec broker mosquitto_sub -v -t 'fleet/mober-fleet-01/vehicles/+/events'

# 3. Enviar comando de desbloqueio (unlock) à mober-bike-0001
docker compose exec broker mosquitto_pub -t 'fleet/mober-fleet-01/vehicles/mober-bike-0001/commands/unlock' -m '{
  "schemaVersion": "1.0.0",
  "messageId": "c3a1f8d0-6a24-4f67-b1d2-9e3e4a5b6c03",
  "type": "command",
  "timestamp": "2026-11-27T18:00:00Z",
  "fleetId": "mober-fleet-01",
  "vehicleId": "mober-bike-0001",
  "data": {
    "commandId": "e5b2a9c1-0d3f-4a8e-8b77-1c2d3e4f5a04"
  }
}'
```
