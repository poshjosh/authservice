---
path: "./customize-raw-jhipster-application.md"
date: "2020-12-17T11:19:00"
title: "Update a Jhipster application with spring-boot-oauth application logic"
description: "Update a raw Jhipster application with spring-boot-oauth application logic maintained separately"
tags: ["Jhipster", "Spring Boot", "OAuth"]
lang: "en-us"
---

ADD UNIQUE CONSTRAINTS
RUN LIQUIBASE SYNC

Good to read: https://www.jhipster.tech/separating-front-end-and-api/

### CUSTOMIZE A NEWLY CREATED JHIPSTER APPLICATION FOR WEBSTORE

- The target application is the jhipster application we want to configure for use
- The source application is the application containing webform developed stuff, in
this case we use an example application named `mywebsite` with base package `com.mywebsite`.
- In this example, both source and target applications have base package of
`com.mywebsite`.

__Generate the jhipster application__

- Open a command prompt

- Create the application folder

- Change to the application folder and enter the following command:

```sh
jhipster
```

- Enter answers to the prompted questions as appropriate.

- The `jhipster` command should generate a new jhipster project

__Import domain__

- The domain file should have already been created.

- Copy the domain file to the root folder of the newly created jhipster application.

- Use the following command to import it e.g file named `domain.jdl` in the
root folder of the project.

```sh
jhipster import-jdl domain.jdl
```

- After importing the domain info.

  * Run the app
  ```sh
  mvnw clean
  mvnw
  ```

  * Enter `Ctrl + C` to stop the app.

  * Commit to git

  * Add the relevant jpa annotations to your entity e.g for unique columns:
  `@Column(unique = true)`

  * Run `mvnw clean` before starting the app again.

__Update pom.xml__

Add the following to the pom.xml

```xml
        <dependency>
            <groupId>nz.net.ultraq.thymeleaf</groupId>
            <artifactId>thymeleaf-layout-dialect</artifactId>
        </dependency>
        <dependency>    
            <groupId>com.looseboxes</groupId>
            <artifactId>spring-boot-oauth</artifactId>
            <version>0.0.1</version>
        </dependency>
```      

- Please use the latest version of the above dependencies.

Add your docker username to the image name in jib plugin of pom.xml

Change:

```xml
<plugin>
    <groupId>com.google.cloud.tools</groupId>
    <artifactId>jib-maven-plugin</artifactId>
    <version>${jib-maven-plugin.version}</version>
    <configuration>

        ...

        <to>
            <image>APP_NAME:latest</image>
        </to>

        ...

    </configuration>
</plugin>    
```

to:

```xml
<plugin>
    <groupId>com.google.cloud.tools</groupId>
    <artifactId>jib-maven-plugin</artifactId>
    <version>${jib-maven-plugin.version}</version>
    <configuration>

        ...

        <to>
            <image>DOCKER_USER_NAME/APP_NAME:latest</image>
        </to>

        ...

    </configuration>
</plugin>    
```

__Update `ApplicationProperties.class`__

__Update .gitignore  and README files__

__Commit to git__

__Transfer Application Code__

- Copy all the source files (unique to the source application) from the source
application to the target application.

- Remember to copy test files.

__Add custom repositories__

If you have written any custom repository, make sure the actual repository
interface extends your custom repository.

__Update EnumTppe__

IF NEED, change all `EnumType.STRING` in source files to `EnumType.ORDINAL` or vice - versa.

__Update SecurityConfiguration__

1.  Add OAuth

If you decide to use oauth2 authentication, and did not specify oauth as your 
authenticaton mechanism when creating the jhipster project, then you need 
to allow session management which is required by Spring Boot OAuth2. To 
allow session management, comment out the following if present.

```
//        .and()
//            .sessionManagement()
//            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
```

Also add the following:

```
//        .and()
//            .sessionManagement()
//            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
            .authorizeRequests()
             // BEGIN ADD
            .antMatchers(Endpoints.OAUTH2_SUCCESS).permitAll()
            .antMatchers(Endpoints.OAUTH2_FAILURE).permitAll()
            // END ADD
            .antMatchers("/api/authenticate").permitAll()
        .
        .
        .
        .and()
            .oauth2Login()
                .loginPage(Endpoints.LOGIN)
                .defaultSuccessUrl(Endpoints.OAUTH2_SUCCESS, true)
                .failureUrl(Endpoints.OAUTH2_FAILURE)
```

__Update AccountResource__

- First add `private final NewUserMessageTemplate newUserMessageTemplate`;

- Then use the `NewUserMessageTemplate` to return a message from the
`registerAccount` method as shown below:

```java
@PostMapping("/register")
@ResponseStatus(HttpStatus.CREATED)
public Map<String, String> registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM) {
    if (!checkPasswordLength(managedUserVM.getPassword())) {
        throw new InvalidPasswordException();
    }
    User user = userService.registerUser(managedUserVM, managedUserVM.getPassword());
    mailService.sendActivationEmail(user);
    return Collections.singletonMap("message", this.newUserMessageTemplate.getContent(user));
}
```

__Update UserService__

- Edit `UserService` to update the corresponding `UserInfo` after a `User` is
`activated`, `created`, `updated` or `deleted`. This update is achieved via
the following imported springframework component:

```java
@Autowired private UserToUserInfoSync userToUserInfoSync;
```

Note. No need to include the code when a user is registered as the user will
be saved when activated.

Here is an example of using the above instance after a user is activated.

```java  
public class UserService{

    @Autowired private UserToUserInfoSync userToUserInfoSync;

    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        return userRepository.findOneByActivationKey(key)
            .map(user -> {
                // activate given user for the registration key.
                user.setActivated(true);
                user.setActivationKey(null);
                this.clearUserCaches(user);
                log.debug("Activated user: {}", user);

                userToUserInfoSync.saveUserInfo(UserService.this, user);

                return user;
            });
    }
}

```

__Update MailService__

Sending email with the default configuration often fails. Google frowns on
less secure apps, even when allow less secure apps is turned on in settings.
So we use gmail client for java.

Replace `MailService` with `MailServiceExt` in `AccountResource`

__Update ManagedUserVM__

In `com.looseboxes.web.rest.vm.ManagedUserVM` update to whatever value. We use `6`.

```java
public static final int PASSWORD_MIN_LENGTH = 4;
```

__Update ExceptionTranslator__

Update the method `...web.rest.errors.ExceptionTranslator#handleMethodArgumentNotValid` to look thus:

```java
    @Override
    public ResponseEntity<Problem> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, @Nonnull NativeWebRequest request) {

        BindingResult result = ex.getBindingResult();

        List<FieldErrorVM> fieldErrors = result.getFieldErrors().stream()
            .map(f -> new FieldErrorVM(f.getObjectName().replaceFirst("DTO$", ""), f.getField(), f.getCode()))
            .collect(Collectors.toList());
        
        String fieldNames = result.getFieldErrors().stream()
                .map((f) -> f.getField()).collect(Collectors.joining(", "));
        
        String detail = result.getFieldErrors().stream()
                .map((f) -> f.getField() + "=" + f.getDefaultMessage())
                .collect(Collectors.joining(", "));

        Problem problem = Problem.builder()
            .withType(ErrorConstants.CONSTRAINT_VIOLATION_TYPE)
            .withTitle("Invalid " + fieldNames)
            .withDetail(detail)
            .withStatus(defaultConstraintViolationStatus())
            .with(MESSAGE_KEY, ErrorConstants.ERR_VALIDATION)
            .with(FIELD_ERRORS_KEY, fieldErrors)
            .build();

        return create(ex, problem, request);
    }
```


__Transfer Static Resources__

- Copy the files in the directory `src/main/resources/`
(excluding file `application.properties` and all sub-directories) from the
source application to the same location in the target application.

- Merge `application.properties` from the source and target application
directory `src/main/resources/`.

- Copy the files in the directory `src/main/resources/templates/` from the
source application to the same location in the target application.

- Copy the files in the directory `src/main/resources/static/` of the source
application to the `src/main/webapp/content/` folder of the target application.

- For template (`.html`) files in the directory `src/main/resources/templates/`
access static resources like style sheets and javascript files via `/content/[RESOURCE_Name]`
e.g `/content/styles.css` for style sheet and `/content/scripts.js` for javascript.

- In `src/main/resources/i18n` update the application name for example if
the jhipster application name is `webapp` and you want to deploy an application
named `jumpersclub` then find and replace all occurrences of the text `webapp`
with `jumpersclub` in the folder `src/main/resources/i18n`

__Some important properties__

Some properties you should definitely provide values for:

```yml
spring:
  datasource:
    username:
    password:
spring:
  mail:
    username:
    password:
    host: smtp.gmail.com
    port: 465
    properties:
      mail:
        debug: false
        transport:
          protocol: smtps
        smtp:
          starttls:
            enable: true
          host: ${spring.mail.port}
          port: ${spring.mail.host}
          quitwait: false
          auth: true
          ssl:
            enable: true
```

for staging:

__Properties to add to properties files__

- In `application.yml` add the following:

```yml
spring:
  servlet:
    multipart:
      enabled: true
      file-size-threshold: 100KB
      max-file-size: 5MB
      max-request-size: 25MB
  data:
    rest:
      base-path: /api
  main:
    banner-mode: off

# Application specific properties
webshop:
  dir: ${user.home}\\.webshop
  user:
    system:
      email: [Add a value here]
    admin:
      email: ${webshop.user.system.email}

# Payment properties
payment:
  voguepay:
    merchantid:
    username:
    email:
    commandapitoken:
    publickeypath:

  image:
    logo: /images/logo.png
    noImage: /images/no-image.png
    loadFailed: /images/no-imag.png
    preferredWidth: 512    
    preferredHeight: ${webshop.image.preferredWidth}
    textOverlay: ${spring.application.name}

bcfileupload:
  outputDir: ${webshop.dir}/uploaded_files      
```

- In `application-dev.yml` add:

```yml
debug: true
```
__Add license__

In `package.json` change:

```json
{"license": "UNLICENSED"}
```

to:

```json
{"license": "SEE LICENSE IN LICENSE.txt"}
```

__Misc__

- Remove banner.txt from the `src/main/resources/` directory.

__Some properties to note__

- `application.yml`

  * Update `spring.application.name` to: `[THE APPLICATION NAME YOU WANT]`
  * Update `jhipster.clientApp.name` to: `${spring.application.name}`
  * `jhipster.mail.from: <appname>@localhost`

  Some notable properties

  * `jhipster.swagger.title: `
  * `jhipster.swagger.description: `
  * `spring.main.allow-bean-definition-overriding: true`
  * `spring.mvc.favicon.enabled: false`

- `application-dev.yml`, `application-prod.yml`

  If you intend to use mail service, provide values for the following.

  * `jhipster.mail.base-url: `
  * `spring.mail.host: localhost`
  * `spring.mail.port: 25`
  * `spring.mail.username: `
  * `spring.mail.password: `

  Some notable properties

  * `spring.jpa.show-sql: true`
  * `server.port: 8080`
