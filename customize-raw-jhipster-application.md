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

`jhipster`

- Enter answers to the prompted questions as appropriate.

- The `jhipster` command should generate a new jhipster project

__Import domain__

- The domain file should have already been created.

- Copy the domain file to the root folder of the newly created jhipster application.

- Use the following command to import it e.g file named `domain.jdl` in the
root folder of the project.

`jhipster import-jdl domain.jdl`

- After importing the domain info.

  * Commit to git

  * Add change log file: `<yyyyMMddHHmmss>_added_entity_constraints_unique.xml`
  and any other change logs to folder: `src/main/resources/config/liquidbase/changelogs/`.
  Also update `src/main/resources/config/liquidbase/master.xml` accordingly.

  * Add the relevant jpa annotations to your entity e.g for unique columns:
  `@Column(unique = true)`

  * Run `mvnw clean` before starting the app again.

__Rename Model entity__

The name of `ModelMapperImpl` class generated for `ModelMapper` interface
of `Model` entity conflicts with a bean found in jhipster class
`io.github.jhipster.config.apidoc.SwaggerAutoConfiguration` This caused errors
when an attempt was made to build the application. Therefore, before building,
rename `Model` interface located in the `com.looseboxes.webshop.service.mapper`
directory, e.g to `ModelMapperRenamedDueToConflictWithSwaggerBean` as shown below:

```java
@Mapper(componentModel = "spring", uses = {ProductSubcategoryMapper.class, BrandMapper.class})
public interface ModelMapperRenamedDueToConflictWithSwaggerBean extends EntityMapper<ModelDTO, Model> {
}
```

Also rename the test class of the above.

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
        <dependency>
            <groupId>com.looseboxes</groupId>
            <artifactId>bcfileclient</artifactId>
            <version>0.0.1</version>
        </dependency>
        <dependency>
            <groupId>com.looseboxes</groupId>
            <artifactId>bcsecurity</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>com.looseboxes</groupId>
            <artifactId>gmailapi</artifactId>
            <version>1.0</version>
        </dependency>
        <dependency>
            <groupId>com.looseboxes</groupId>
            <artifactId>webform</artifactId>
            <version>1.1.3</version>
        </dependency>
        <dependency>
            <groupId>com.looseboxes</groupId>
            <artifactId>voguepay-java-client</artifactId>
            <version>1.0-SNAPSHOT</version>
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

__Update SpringBootApplication__

- Update the `@SpringBootApplication` annotation as shown below:

```java
@SpringBootApplication(scanBasePackageClasses = {
        com.mywebsite.MywebsiteApp.class,
        com.looseboxes.webform.WebformBasePackageClass.class
})
@EnableConfigurationProperties({LiquibaseProperties.class, WebstoreProperties.class})
```

Where `com.mywebsite.MywebsiteApp.class` refers to the application class of the
target application.

Also make sure you replace `ApplicationProperties.class` with `WebstoreProperties.class`

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
        .and()
            .oauth2Login()
                .loginPage(Endpoints.LOGIN)
                .loginPage(Endpoints.LOGIN)
                .defaultSuccessUrl(Endpoints.OAUTH2_SUCCESS, true)
                .failureUrl(Endpoints.OAUTH2_FAILURE)
```

2. Update configuration

Update the security configuration of the target application as shown, adding
the portion(s) marked as added, and removing those marked as remove.

Below is an extract of an actual `SecurityConfiguration.java` file.

```java
public void configure(HttpSecurity http) throws Exception {
    http
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    .and()
      .authorizeRequests()
             // BEGIN ADD
            .antMatchers(Endpoints.IMAGES + "/**/*").permitAll()              
            .antMatchers(Endpoints.API_IMAGES + "/**/*").permitAll()              
            .antMatchers(Endpoints.ACCOUNT + "/**").permitAll()
            .antMatchers(Endpoints.API_EXT + Endpoints.WEB_DATA).permitAll()          
            .antMatchers(Endpoints.API_EXT + Endpoints.USER_EXISTS).permitAll()
            .antMatchers(Endpoints.API_EXT + Endpoints.USER_EVENT + "/**").permitAll()          
            .antMatchers(Endpoints.SEARCH + "/**").permitAll()                
            .antMatchers(Endpoints.API_EXT + Endpoints.SEARCH + "/**").permitAll()            
            .antMatchers(Endpoints.FROM_PAYMENT_GATEWAY).permitAll()     
            .antMatchers(Endpoints.API_EXT + "/authenticate/**").permitAll()
            .antMatchers(Endpoints.API_EXT + "/register/**").permitAll()
            .antMatchers(Endpoints.API_EXT + "/activate").permitAll()
            .antMatchers(Endpoints.API_EXT + "/account/reset-password/init").permitAll()
            .antMatchers(Endpoints.API_EXT + "/account/reset-password/finish").permitAll()
            // END ADD
            .antMatchers("/api/authenticate").permitAll()
            .antMatchers("/api/register").permitAll()
            .antMatchers("/api/activate").permitAll()
            .antMatchers("/api/account/reset-password/init").permitAll()
            .antMatchers("/api/account/reset-password/finish").permitAll()
            // BEGIN ADD
            .antMatchers(Endpoints.WEBFORM + "/**").authenticated()     
            .antMatchers(Endpoints.API_WEBFORM + "/**").authenticated() 
            .antMatchers(Endpoints.API_EXT + "/**").authenticated()
            .antMatchers(HttpMethod.GET, "/api/**").authenticated()
            .antMatchers("/api/**").hasAuthority(AuthoritiesConstants.ADMIN)
            // END ADD
            // BEGIN REMOVE
//            .antMatchers("/api/**").authenticated()
            // END REMOVE
            .antMatchers("/management/health").permitAll()
            .antMatchers("/management/info").permitAll()
            .antMatchers("/management/prometheus").permitAll()
            .antMatchers("/management/**").hasAuthority(AuthoritiesConstants.ADMIN)
}      
```
_Only the relevant section of the method is displayed above__

__ElasticSearch__

If using Elasticsearch 

Add the following annotation to all enities you want to be searchable:
```java
@org.springframework.data.elasticsearch.annotations.Document(indexName = "address")
```

For example, the following entities were excluded: AccountDetais, Transaction, OrderItem, OrderDetails, PaymentDetails,PaymentMethod

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
