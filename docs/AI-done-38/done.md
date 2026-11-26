# Relatório de Conclusão de Implementação — História SCRUM-38 (Épico 1)

> **História Jira:** `SCRUM-38` (`US-ADM-03: Gestão de Contas de Utilizadores, Permissões (RBAC) e Autenticação`)  
> **Épico Jira:** `SCRUM-29` (`ÉPICO 1: Gestão de Utilizadores e Autenticação`)  
> **Branch de Trabalho:** `SCRUM-38-user-management-and-auth`  
> **Prática SQA Cumprida:** `AI4 (Specification-driven generation)` a partir de `docs/specs/SCRUM-38-user-auth.feature`  
> **Estado do Build:** `BUILD SUCCESS` (32 testes passados, 0 falhas, 0 erros)  
> **Cobertura de Código (JaCoCo):** **90.87%** de instruções, **88.79%** de linhas, **100%** de classes  

---

## 1. Visão Geral da Solução

O objetivo desta implementação foi construir de raiz o módulo completo e seguro de **autenticação, gestão de utilizadores e controlo de acessos por perfil (RBAC — Role-Based Access Control)** no backend em **Spring Boot 4 / Java 21**, garantindo que:
1. O registo de novos utilizadores é validado e as passwords são cifradas com **BCrypt** (nunca em texto limpo).
2. A autenticação (login) emite **JSON Web Tokens (JWT)** assinados digitalmente com HMAC SHA-256 e tempo de expiração configurado.
3. Os endpoints da API REST são protegidos em conformidade com o perfil ativo do utilizador (`ROLE_COURIER`, `ROLE_OPERATOR`, `ROLE_ADMIN`).
4. Utilizadores com o estado `SUSPENDED` são impedidos de iniciar sessão.
5. Operações administrativas (listar utilizadores e suspender contas) são restritas estritamente a administradores (`ROLE_ADMIN`).

---

## 2. Explicação Detalhada do Código de Produção

### 2.1. Modelo de Dados e Persistência JPA
Localização: `src/main/java/pt/ua/tqs/mober/model` e `pt/ua/tqs/mober/repository`

* **`Role.java` (Enum):**
  * Define os 3 perfis do sistema Möber: `ROLE_COURIER` (estafeta), `ROLE_OPERATOR` (técnico de manutenção/recolha) e `ROLE_ADMIN` (administrador do sistema).
* **`UserStatus.java` (Enum):**
  * Define o ciclo de vida da conta: `ACTIVE` (conta ativa e operacional) e `SUSPENDED` (conta bloqueada pelo administrador em caso de incumprimento).
* **`User.java` (Entidade JPA):**
  * Tabela `users` com os campos:
    * `id`: Chave primária auto-gerada (`GenerationType.IDENTITY`).
    * `name`: Nome do utilizador (`nullable = false`).
    * `email`: Endereço único (`unique = true, nullable = false`).
    * `password`: Hash da palavra-passe (`nullable = false`).
    * `role`: Perfil mapeado como String (`@Enumerated(EnumType.STRING)`).
    * `status`: Estado da conta (padrão: `ACTIVE`).
    * `createdAt`: Timestamp de registo imutável (`updatable = false`).
* **`UserRepository.java` (Interface Spring Data JPA):**
  * Métodos declarativos de pesquisa:
    * `Optional<User> findByEmail(String email)`: Usado para localizar o utilizador durante o login e validação do token JWT.
    * `boolean existsByEmail(String email)`: Otimizado para verificar unicidade do email antes do registo.

---

### 2.2. Camada de Segurança, Criptografia e JWT
Localização: `src/main/java/pt/ua/tqs/mober/security`

* **`SecurityConfig.java` (Configuração Central do Spring Security):**
  * **Stateless:** A sessão HTTP tradicional é desativada (`SessionCreationPolicy.STATELESS`), uma vez que cada pedido é autenticado pelo token JWT.
  * **CSRF Desativado:** Por ser uma API REST stateless sem cookies de sessão vulneráveis a CSRF.
  * **Bean `PasswordEncoder`:** Instancia um `BCryptPasswordEncoder` com algoritmo de derivação de chaves seguro.
  * **Regras de Autorização de Rotas (`SecurityFilterChain`):**
    * Rotas públicas (`permitAll`): `/api/auth/**` (registo e login), `/api/status`, `/v3/api-docs/**`, `/swagger-ui/**`.
    * Perfil pessoal (`authenticated`): `/api/users/me` (qualquer utilizador com token válido pode ver o seu próprio perfil).
    * Gestão administrativa (`hasAuthority("ROLE_ADMIN")`): `/api/users/**` (apenas administradores podem listar contas ou alterar o estado de utilizadores).
    * Restantes rotas: requerem autenticação por defeito.
  * Injeta o `JwtAuthenticationFilter` antes do `UsernamePasswordAuthenticationFilter` nativo do Spring.

* **`JwtService.java` (Gestão de Tokens JWT):**
  * Utiliza a biblioteca oficial `jjwt` (versão 0.12.6).
  * **Assinatura:** Chave secreta configurada no `application.properties` (`mober.jwt.secret`) com algoritmo HMAC SHA-256 (`Keys.hmacShaKeyFor`).
  * **`generateToken(User user)`:** Insere no payload do token:
    * `subject`: Email do utilizador.
    * `claims`: `id`, `name` e `role`.
    * `issuedAt` e `expiration`: Validade configurável (padrão: 24 horas / 86400000 ms).
  * **`extractEmail(String token)` e `extractRole(String token)`:** Extrai claims de forma segura validando a integridade da assinatura.
  * **`isTokenValid(String token, User user)`:** Confirma que o token pertence ao utilizador, não expirou e não foi adulterado.

* **`JwtAuthenticationFilter.java` (Filtro por Pedido HTTP):**
  * Extende `OncePerRequestFilter` para garantir uma única execução por pedido.
  * Lê o cabeçalho `Authorization`. Se começar por `Bearer <token>`, extrai o token.
  * Extrai o email e verifica se o utilizador existe na base de dados e se está no estado `ACTIVE`.
  * Se o token for válido e o utilizador estiver ativo, cria um `UsernamePasswordAuthenticationToken` com a autoridade correspondente à sua `Role` e regista-o no `SecurityContextHolder`.

---

### 2.3. Objetos de Transferência de Dados (DTOs)
Localização: `src/main/java/pt/ua/tqs/mober/dto`

* **`RegisterRequest.java`:** Record com validação Jakarta:
  * `name`: `@NotBlank(message = "Name is required")`
  * `email`: `@NotBlank`, `@Email(message = "Invalid email format")`
  * `password`: `@NotBlank`, `@Size(min = 8, message = "Password must have at least 8 characters")`
  * `role`: Campo opcional (se for omitido, assume por defeito `ROLE_COURIER`).
* **`LoginRequest.java`:** Record com `email` e `password` não nulos.
* **`AuthResponse.java`:** Devolve o `token`, o tipo (`Bearer`), o `id`, `name`, `email` e `role`.
* **`UserResponse.java`:** DTO seguro para devolução de perfis e listagens, omitindo terminantemente o campo da password.
* **`UpdateStatusRequest.java`:** Record com `@NotNull UserStatus status` para atualizar o estado de uma conta.

---

### 2.4. Lógica de Negócio (Serviços)
Localização: `src/main/java/pt/ua/tqs/mober/service`

* **`AuthService.java`:**
  * **`register(RegisterRequest)`:**
    1. Verifica se o email já existe através de `userRepository.existsByEmail`. Caso exista, lança `DuplicateEmailException` (HTTP 409 Conflict).
    2. Encripta a password com `passwordEncoder.encode(request.password())`.
    3. Cria e persiste a nova entidade `User` com estado `ACTIVE`.
    4. Gera e devolve o token JWT com `AuthResponse`.
  * **`login(LoginRequest)`:**
    1. Procura o utilizador por email; se não encontrar, lança `BadCredentialsException` (HTTP 401).
    2. Valida a password fornecida contra a hash da base de dados com `passwordEncoder.matches`. Se falhar, lança `BadCredentialsException` (HTTP 401).
    3. Valida se a conta está no estado `ACTIVE`. Se estiver `SUSPENDED`, lança `BadCredentialsException` (HTTP 401).
    4. Gera o token JWT e devolve os dados de sessão.
* **`UserService.java`:**
  * **`getProfile(String email)`:** Devolve os dados do utilizador autenticado em formato `UserResponse`.
  * **`getAllUsers()`:** Lista todos os utilizadores da plataforma em formato `UserResponse` (para a visão do admin).
  * **`updateUserStatus(Long id, UserStatus newStatus)`:** Altera o estado do utilizador (ex.: de `ACTIVE` para `SUSPENDED`).

---

### 2.5. Controladores REST e Tratamento de Exceções
Localização: `src/main/java/pt/ua/tqs/mober/controller` e `pt/ua/tqs/mober/exception`

* **`AuthController.java` (`/api/auth`):**
  * `POST /api/auth/register` $\rightarrow$ Valida com `@Valid`, cria a conta e retorna `201 Created` com o JWT.
  * `POST /api/auth/login` $\rightarrow$ Valida as credenciais e retorna `200 OK` com o JWT.
* **`UserController.java` (`/api/users`):**
  * `GET /api/users/me` $\rightarrow$ Retorna o perfil do utilizador identificado pelo token no contexto de segurança (`Authentication`).
  * `GET /api/users` $\rightarrow$ Retorna a lista de utilizadores (protegido pelo `SecurityConfig` para `ROLE_ADMIN`).
  * `PATCH /api/users/{id}/status` $\rightarrow$ Atualiza o estado da conta (protegido para `ROLE_ADMIN`).
* **`GlobalExceptionHandler.java` (`@RestControllerAdvice`):**
  * `MethodArgumentNotValidException` $\rightarrow$ Retorna `400 Bad Request` com mapa detalhado dos campos inválidos.
  * `DuplicateEmailException` $\rightarrow$ Retorna `409 Conflict`.
  * `BadCredentialsException` $\rightarrow$ Retorna `401 Unauthorized`.
  * `AccessDeniedException` $\rightarrow$ Retorna `403 Forbidden` com mensagem amigável de permissões insuficientes.
  * `ResourceNotFoundException` $\rightarrow$ Retorna `404 Not Found`.

---

## 3. Explicação Detalhada da Suíte de Testes

Alinhado com as exigências de TQS e a especificação BDD, foram implementados testes unitários (com Mockito) e testes de integração com subida do contexto real da aplicação (com MockMvc).

### 3.1. Testes Unitários de Serviços e Segurança

* **`AuthServiceTest.java` (7 testes — JUnit 5 + Mockito):**
  * `registerSuccess`: Testa o fluxo feliz de registo, garantindo que o `userRepository.save` e a encriptação de password são chamados e que é emitido um JWT.
  * `registerWithNullRoleDefaultsToCourier`: Garante que se o perfil for omitido no registo, a aplicação atribui por omissão `ROLE_COURIER`.
  * `registerDuplicateEmailThrowsException`: Garante que tentar registar um email já existente lança `DuplicateEmailException` e não grava nada na BD.
  * `loginSuccess`: Simula a pesquisa do utilizador, a comparação com BCrypt e a emissão do token JWT.
  * `loginUserNotFoundThrowsBadCredentials`: Garante que email inexistente lança `BadCredentialsException`.
  * `loginWrongPasswordThrowsBadCredentials`: Garante que password errada lança `BadCredentialsException`.
  * `loginSuspendedAccountThrowsBadCredentials`: Garante que um utilizador com estado `SUSPENDED` é bloqueado no login.

* **`JwtServiceTest.java` (4 testes — JUnit 5):**
  * `generateTokenAndExtractClaims`: Testa a geração de um token e verifica que `extractEmail` e `extractRole` devolvem exatamente os dados originais.
  * `isTokenValidReturnsTrueForMatchingUser`: Garante que a validação é verdadeira para o utilizador titular.
  * `isTokenValidReturnsFalseForDifferentUser`: Garante que a validação falha para um utilizador diferente.
  * `isTokenValidReturnsFalseForMalformedToken`: Garante que tokens adulterados ou mal formados são rejeitados de imediato.

* **`UserServiceTest.java` (5 testes — JUnit 5 + Mockito):**
  * `getProfileSuccess` e `getProfileNotFoundThrowsException`: Validação de obtenção de perfil e tratamento de utilizador inexistente.
  * `getAllUsersReturnsList`: Testa a conversão de lista de entidades `User` para lista de DTOs `UserResponse`.
  * `updateUserStatusSuccess`: Testa a alteração de estado para `SUSPENDED`.
  * `updateUserStatusNotFoundThrowsException`: Valida tentativa de atualizar utilizador inexistente.

---

### 3.2. Testes de Integração de API e Segurança (MockMvc)

* **`AuthControllerIntegrationTest.java` (8 testes — `@SpringBootTest` + `@AutoConfigureMockMvc`):**
  * `registerSuccess`: Dispara um `POST /api/auth/register` HTTP real contra o MockMvc e valida status `201 Created` e retorno do JWT Bearer.
  * `registerWithInvalidEmailFailsWith400`: Testa validação com email mal formatado e verifica mensagem de erro do campo no JSON.
  * `registerWithBlankNameFailsWith400`: Testa validação com nome vazio.
  * `registerWithShortPasswordFailsWith400`: Testa validação de password com menos de 8 caracteres.
  * `registerDuplicateEmailFailsWith409`: Guarda previamente um utilizador na BD H2 em memória e dispara um segundo registo com o mesmo email, verificando o status `409 Conflict`.
  * `loginSuccess`: Dispara `POST /api/auth/login` com credenciais válidas e verifica status `200 OK` e dados do token.
  * `loginWithWrongPasswordFailsWith401`: Dispara login com password errada e verifica status `401 Unauthorized`.
  * `loginWithSuspendedAccountFailsWith401`: Testa login com conta em estado `SUSPENDED` e verifica rejeição com status `401 Unauthorized`.

* **`UserControllerSecurityIntegrationTest.java` (6 testes — RBAC e Proteção de Endpoints):**
  * `getMeWithoutTokenReturnsUnauthorized`: Tenta aceder a `GET /api/users/me` sem cabeçalho Authorization e verifica retorno de `401 Unauthorized`.
  * `getMeWithCourierTokenReturnsProfile`: Envia cabeçalho `Authorization: Bearer <token_courier>` e valida acesso com status `200 OK`, confirmando que a password não é devolvida no JSON.
  * `getAllUsersWithCourierTokenReturnsForbidden`: Um utilizador comum (`COURIER`) tenta aceder a `GET /api/users` e o Spring Security bloqueia com `403 Forbidden`.
  * `getAllUsersWithAdminTokenReturnsUserList`: Um utilizador administrador (`ADMIN`) acede a `GET /api/users` e obtém `200 OK` com a listagem de todos os utilizadores.
  * `updateUserStatusWithCourierTokenReturnsForbidden`: Um utilizador `COURIER` tenta suspender outro utilizador e é bloqueado com `403 Forbidden`.
  * `updateUserStatusWithAdminTokenSuccess`: Um administrador envia `PATCH /api/users/{id}/status` com `SUSPENDED` e o pedido é aceite com `200 OK`, atualizando a base de dados.

* **`StatusEndpointTest.java` & `MoberBackendApplicationTests.java` (2 testes):**
  * Validação do endpoint `/api/status` e carregamento integral do contexto Spring da aplicação.

---

## 4. Métricas de Cobertura de Código (JaCoCo)

Após a execução completa de `./mvnw clean verify`:

| Métrica | Cobertura Obtida | Limiar Exigido (TQS) | Estado |
| :--- | :---: | :---: | :---: |
| **Instruções (Bytecode)** | **90.87%** (886/975) | $\ge$ 80% | **APROVADO** |
| **Linhas de Código** | **88.79%** (198/223) | $\ge$ 80% | **APROVADO** |
| **Branches de Decisão** | **75.00%** (21/28) | — | **APROVADO** |
| **Classes Cobertas** | **100.00%** (20/20) | — | **APROVADO** |

O relatório JaCoCo foi gerado em `backend/target/site/jacoco/jacoco.xml`, pronto para ser consumido pela ação de análise do **SonarCloud** no GitHub Actions.

---

## 5. Mapeamento com as Subtarefas do Jira (História SCRUM-38)

Todas as subtarefas planeadas no Jira encontram-se concluídas:

1. `Configuração de dependências e segurança (Spring Security & JWT)` $\longrightarrow$ **Concluído** (`pom.xml` atualizado com Spring Security, JPA, Validation, H2 e JJWT).
2. `Modelo de dados: Entidade User, Enum Role e Repositório JPA` $\longrightarrow$ **Concluído** (`User.java`, `Role.java`, `UserStatus.java`, `UserRepository.java`).
3. `Serviço de autenticação e filtros JWT (BCrypt e RBAC)` $\longrightarrow$ **Concluído** (`JwtService.java`, `JwtAuthenticationFilter.java`, `SecurityConfig.java`).
4. `Endpoints REST de registo e login com validação de dados` $\longrightarrow$ **Concluído** (`AuthController.java`, `RegisterRequest.java`, `LoginRequest.java`, `AuthResponse.java`).
5. `Endpoints de gestão de utilizadores e suspensão de contas (Admin)` $\longrightarrow$ **Concluído** (`UserController.java`, `UserService.java`, `UserResponse.java`).
6. `Testes unitários de serviços e segurança JWT (Mockito)` $\longrightarrow$ **Concluído** (16 testes unitários em `AuthServiceTest`, `JwtServiceTest`, `UserServiceTest`).
7. `Testes de integração de API e controlo de acessos (MockMvc)` $\longrightarrow$ **Concluído** (14 testes de integração em `AuthControllerIntegrationTest`, `UserControllerSecurityIntegrationTest`).
