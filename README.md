# Möber

[![Backend CI](https://github.com/Mober-TQS/Mober/actions/workflows/backend-ci.yml/badge.svg?branch=main)](https://github.com/Mober-TQS/Mober/actions/workflows/backend-ci.yml)

## 1. Project Abstract

O **Möber** é uma plataforma de mobilidade suave no modelo *Fleet-as-a-Service* (FaaS), projetada especificamente para responder às exigências operacionais de estafetas de entregas urbanas (trabalhadores de plataformas como a Uber
Eats). Ao contrário de sistemas urbanos convencionais focados em deslocações esporádicas, o Möber oferece um serviço contínuo orientado a turnos de trabalho prolongados e sem interrupções.

### Executar o backend

Com o JDK 21 instalado, executa a partir da raiz do repositório:

```bash
cd backend
./mvnw spring-boot:run
```

Com a aplicação em execução, consulta o estado num segundo terminal:

```bash
curl -i http://localhost:8080/api/status
```

A resposta esperada tem o código HTTP `200` e o corpo JSON `{"status":"ok"}`.

Para parar a aplicação, usa `Ctrl+C` no primeiro terminal. Nesse terminal, ainda na pasta `backend/`, executa os testes e gera o JAR com o mesmo objetivo Maven usado no CI:

```bash
./mvnw clean verify
```

---

## 2. Project Team

Todos os membros da equipa desempenham ativamente a função de **Developer**, acumulando as responsabilidades de liderança e garantia de qualidade definidas no âmbito da metodologia de engenharia de software do projeto:

| Nome | Número Mec. | Papel |
| :--- | :---: | :--- |
| `Vitalie Bologa` | `107854` | **Team Coordinator** |
| `Tiago Costa` | `125943` | **Product Owner** |
| `Filipe Nogueira` | `119495` | **QA Engineer** |
| `Margarida Cardoso` | `125799` | **DevOps Master** |

---

## 3. Project Bookmarks

* **Gestão de Projeto & Backlog:** [Jira Backlog]([URL_A_DEFINIR])
* **Ambiente de Produção / Aplicação:** [Möber Deployed Application]([URL_A_DEFINIR])
* **Documentação de Engenharia & APIs:** [OpenAPI / Swagger UI Documentation]([URL_A_DEFINIR])
* **Qualidade de Código & Análise Estática:** [Dashboard do Mober no SonarQube Cloud](https://sonarcloud.io/dashboard?id=Mober-TQS_Mober)
* **Observabilidade & Métricas:** [Grafana & Prometheus Dashboard]([URL_A_DEFINIR])
* **Relatório de Especificação de Produto:** [Product Specification Report (PDF)]([URL_A_DEFINIR])
* **Manual de Garantia de Qualidade:** [Quality Assurance Manual (PDF)]([URL_A_DEFINIR])