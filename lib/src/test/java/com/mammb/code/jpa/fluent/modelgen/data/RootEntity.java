package com.mammb.code.jpa.fluent.modelgen.data;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
public class RootEntity extends SuperEntity {

    private String name;

    @Enumerated(EnumType.STRING)
    private EnumValue enumValue;

    @Embedded
    private ValueObject valueObject;

    @OneToOne
    private ChildEntity childEntity;

    @ManyToOne
    private ChildEntity child;

    @OneToMany
    private List<ChildEntity> childrenList;

    @OneToMany
    private Set<ChildEntity> childrenSet;

    @OneToMany
    private Collection<ChildEntity> childrenCollection;

    @OneToMany
    private Map<String, ChildEntity> childrenMap;

}
