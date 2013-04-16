package dst.ass2.di.type;

import dst.ass2.di.annotation.Component;
import dst.ass2.di.annotation.ComponentId;
import dst.ass2.di.annotation.Inject;
import dst.ass2.di.model.ScopeType;

@Component(scope = ScopeType.PROTOTYPE)
@SuppressWarnings("unused")
public abstract class SuperType {
    @ComponentId
    private Long id;
    @Inject
    private SimpleComponent component;
}
