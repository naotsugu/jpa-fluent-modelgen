package com.mammb.code.jpa.fluent.modelgen;

import com.mammb.code.jpa.fluent.modelgen.data.RootImpl;
import com.mammb.code.jpa.fluent.modelgen.data.Root_;
import org.junit.jupiter.api.Test;

class JpaMetaModelEnhanceProcessorTest {

    @Test
    void test() {
        var root = Root_.rootEntity(new RootImpl<>());
        root.getName();
        root.joinChildrenList();
        root.getChildrenList();
        root.joinChildrenSet();
        root.getChildrenSet();
        root.joinChildrenMap();
        root.getChildrenMap();
    }

}
