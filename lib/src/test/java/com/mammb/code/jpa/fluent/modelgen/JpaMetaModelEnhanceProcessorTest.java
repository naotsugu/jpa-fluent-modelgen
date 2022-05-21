package com.mammb.code.jpa.fluent.modelgen;

import com.mammb.code.jpa.fluent.modelgen.data.RootEntity_Root_;
import com.mammb.code.jpa.fluent.modelgen.data.RootImpl;
import org.junit.jupiter.api.Test;

class JpaMetaModelEnhanceProcessorTest {

    @Test
    void test() {
        var root = new RootEntity_Root_(new RootImpl<>());
        root.getName();
        root.joinChildrenList();
        root.getChildrenList();
        root.joinChildrenSet();
        root.getChildrenSet();
        root.joinChildrenMap();
        root.getChildrenMap();
    }

}
