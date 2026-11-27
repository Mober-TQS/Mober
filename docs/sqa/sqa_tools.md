# Core SQA Tools

Este documento lista todas as ferramentas de Garantia de Qualidade (SQA) escolhidas pela equipa para as práticas obrigatórias (`E`) e valorizadas (`V`) do guião de TQS.

---

## 1. Ferramentas Core Escolhidas (Obrigatórias / Padrão)

<table>
  <thead>
    <tr>
      <th>Prática (Guião)</th>
      <th>Categoria</th>
      <th>Ferramenta Escolhida</th>
      <th>Onde se aplica / Notas</th>
    </tr>
  </thead>
  <tbody>
    <tr style="background-color: rgba(46, 160, 67, 0.22);">
      <td><b>PM1 – PM4</b></td>
      <td>Gestão Ágil &amp; Git</td>
      <td><b>Jira + GitHub</b></td>
      <td>Backlog, Sprints, Story Points, branches e commits com chave <code>SCRUM-XX</code>.</td>
    </tr>
    <tr style="background-color: rgba(210, 153, 34, 0.22);">
      <td><b>PM5</b></td>
      <td>Gestão de Testes &amp; Cobertura de Requisitos</td>
      <td><b>Jira + Xray</b></td>
      <td>Ligação entre User Stories e Testes; importação de resultados do CI.</td>
    </tr>
    <tr style="background-color: rgba(210, 153, 34, 0.22);">
      <td><b>T1</b></td>
      <td>Testes de Aceitação / BDD</td>
      <td><b>Cucumber (Gherkin)</b></td>
      <td>Ficheiros <code>.feature</code> com cenários <code>Given / When / Then</code> para as histórias principais.</td>
    </tr>
    <tr style="background-color: rgba(46, 160, 67, 0.22);">
      <td><b>T2</b></td>
      <td>Testes Unitários</td>
      <td><b>JUnit 5 + Mockito</b> (+ AssertJ)</td>
      <td>Testes unitários à lógica de negócio (Maven Surefire).</td>
    </tr>
    <tr style="background-color: rgba(210, 153, 34, 0.22);">
      <td><b>T3</b></td>
      <td>Testes de Integração &amp; API</td>
      <td><b>Spring Boot Test + Testcontainers + REST Assured</b></td>
      <td>Separados dos testes unitários no build (Maven Failsafe); testa BD PostgreSQL real, endpoints REST e adapters.</td>
    </tr>
    <tr style="background-color: rgba(210, 153, 34, 0.22);">
      <td><b>T4</b></td>
      <td>Testes End-to-End (Web UI)</td>
      <td><b>Playwright</b></td>
      <td>Testes E2E da interface web (<code>@UsePlaywright</code>); não invocado em todos os builds.</td>
    </tr>
    <tr style="background-color: rgba(210, 153, 34, 0.22);">
      <td><b>T5</b></td>
      <td>Testes de Carga / SLOs</td>
      <td><b>k6</b></td>
      <td>Validação automática dos SLOs definidos no <i>Product Specs</i> com <i>thresholds</i>.</td>
    </tr>
    <tr style="background-color: rgba(46, 160, 67, 0.22);">
      <td><b>Q1, Q2</b></td>
      <td>Cobertura de Código &amp; Análise Estática</td>
      <td><b>JaCoCo + SonarCloud</b></td>
      <td>Dashboard de qualidade na cloud e <b>Quality Gate bloqueante</b> no código novo. <b>Nota:</b> o repositório <code>Mober-TQS/Mober</code> mantém-se <b>público</b> para o plano gratuito do SonarCloud.</td>
    </tr>
    <tr style="background-color: rgba(210, 153, 34, 0.22);">
      <td><b>Q3</b></td>
      <td>Scanning de Vulnerabilidades em Dependências</td>
      <td><b>Dependabot (+ <code>dependency-review-action</code>)</b></td>
      <td>Monitorização de dependências de backend (<code>pom.xml</code>) e frontend (<code>package.json</code>) e validação automática nos Pull Requests.</td>
    </tr>
    <tr style="background-color: rgba(46, 160, 67, 0.22);">
      <td><b>Q4</b></td>
      <td>Revisão de Código</td>
      <td><b>GitHub Pull Requests</b></td>
      <td>Regra de aprovação por par antes do merge na <code>main</code>.</td>
    </tr>
    <tr style="background-color: rgba(210, 153, 34, 0.22);">
      <td><b>Q5, AI1 – AI5</b></td>
      <td>Guidelines &amp; IA na Engenharia de Qualidade</td>
      <td><b>Política de IA + <code>AGENTS.md</code> + Checklist</b></td>
      <td>Regras gerais, estilo de código, checklist de review e geração <i>spec-driven</i> (&gt;= 2 stories) definidos em <code>AGENTS.md</code> e no QA Manual.</td>
    </tr>
  </tbody>
</table>

---

## 2. Práticas Valorizadas (`V` — Escolhidas)

<table>
  <thead>
    <tr>
      <th>Prática (Guião)</th>
      <th>Categoria</th>
      <th>Ferramenta Escolhida</th>
      <th>Onde se aplica / Notas</th>
    </tr>
  </thead>
  <tbody>
    <tr style="background-color: rgba(46, 160, 67, 0.22);">
      <td><b>T6</b></td>
      <td>TDD (Test-Driven Development)</td>
      <td><b>JUnit 5 + Mockito</b></td>
      <td>Commits <i>test-first</i> em componentes selecionados da lógica de negócio.</td>
    </tr>
    <tr style="background-color: rgba(210, 153, 34, 0.22);">
      <td><b>T7</b></td>
      <td>Pairwise Testing</td>
      <td><b>PICT</b></td>
      <td>Testes combinatórios para regras com várias variáveis que interagem (bateria, estado do veículo, tipo de reserva, lotação do hub).</td>
    </tr>
    <tr style="background-color: rgba(210, 153, 34, 0.22);">
      <td><b>T8</b></td>
      <td>Acessibilidade Automatizada (Web UI)</td>
      <td><b>axe-core (dentro do Playwright)</b></td>
      <td>Integrado diretamente nos testes E2E de Playwright (<code>AxeBuilder</code>), reutilizando o browser e os fluxos testados.</td>
    </tr>
    <tr style="background-color: rgba(210, 153, 34, 0.22);">
      <td><b>T9</b></td>
      <td>Contract Test (GBFS)</td>
      <td><b>JSON Schema Validator</b></td>
      <td>Validação do feed público GBFS v3.0 contra o JSON Schema oficial.</td>
    </tr>
    <tr style="background-color: rgba(210, 153, 34, 0.22);">
      <td><b>T10</b></td>
      <td>Testes Não-Funcionais Extra</td>
      <td><b>k6</b></td>
      <td>Testes de fluxo contínuo, picos (<i>spikes</i>) e <i>stress</i> até ao limite em k6.</td>
    </tr>
    <tr style="background-color: rgba(210, 153, 34, 0.22);">
      <td><b>Q6</b></td>
      <td>Segurança de Contentores</td>
      <td><b>Trivy (<code>aquasecurity/trivy-action</code>)</b></td>
      <td>Análise de vulnerabilidades nas imagens Docker no GitHub Actions.</td>
    </tr>
    <tr style="background-color: rgba(210, 153, 34, 0.22);">
      <td><b>D6</b></td>
      <td>Observabilidade</td>
      <td><b>Spring Boot Actuator + Prometheus + Grafana</b></td>
      <td>Recolha de métricas em tempo real, dashboards e alertas sobre SLOs.</td>
    </tr>
  </tbody>
</table>
