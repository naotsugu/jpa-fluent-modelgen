# JPA Fluent Metamodel Generator

[![Build](https://github.com/naotsugu/jpa-fluent-modelgen/actions/workflows/gradle-build.yml/badge.svg)](https://github.com/naotsugu/jpa-fluent-modelgen/actions/workflows/gradle-build.yml)


## What is this

A simple annotation processor that enhances JPA static metamodel.

Using JPA static metamodel, the code would look like this :

```java
cb.equal(root.join(Customer_.organizations)
        .get(Organization_.address)
        .get(Address_.zipCode)
        .get(zipCode_.code),
    zipCode);
```

At this time, the IDE's code completion cannot be utilized.


With this library, you can write the following to take advantage of code completion.

```java
root(root, query, cb).joinOrganizations()
    .getAddress().getZipCode().getCode().eq(zipCode);
```

## Usage

If you use `javax.persistence` API and Gradle Kotlin DSL, define annotation processors as follows :

```kotlin
dependencies {
  annotationProcessor("org.hibernate:hibernate-jpamodelgen:5.6.10.Final")
  annotationProcessor("com.mammb:jpa-fluent-modelgen:0.9.0")
}
```

If you use `jakarta.persistence` API, do the following

```kotlin
dependencies {
  annotationProcessor("org.hibernate.orm:hibernate-jpamodelgen:6.1.2.Final")
  annotationProcessor("com.mammb:jpa-fluent-modelgen:0.9.0")
}
```

Other static metamodel generation libraries can be used.


## Use with Spring Boot

Use JpaSpecificationExecutor to define the repository.

```java
public interface CustomerRepository extends JpaRepository<Customer, Long>,
    JpaSpecificationExecutor<Customer> {
}
```

Create Specification Helper.

```java
public class CustomerSpecs {

    public static Specification<Customer> firstNameLike(String firstName) {
        return (root, query, cb) -> root(root, query, cb)
            .getFirstName().like(firstName);
    }

    public static Specification<Customer> organizationZipEq(String zipCode) {
        return (root, query, cb) -> {
            query.distinct(true);
            return root(root, query, cb).joinOrganizations()
                .getAddress().getZipCode().getCode().eq(zipCode);
        };
    }
}
```

Then you can query as follows

```java
public List<Customer> findByFirstName(String firstName) {
    return repository.findAll(CustomerSpecs.firstNameLike(firstName));
}

public List<Customer> findByOrganZip(String code) {
    return repository.findAll(CustomerSpecs.organizationZipEq(code));
}

public List<Customer> findByFirstNameAndOrganZip(String firstName, String code) {
    return repository.findAll(CustomerSpecs.organizationZipEq(code)
        .and(CustomerSpecs.firstNameLike(firstName)));
}
```

See `example/spring-boot` for more details.


## Use with JPA Fluent Query

When used with `JPA Fluent Query`, it allows for flexible query construction.

See [JPA Fluent Query](https://github.com/naotsugu/jpa-fluent-query) for more details.
