package dst.ass2.di.type;

import dst.ass2.di.annotation.Component;
import dst.ass2.di.annotation.ComponentId;
import dst.ass2.di.annotation.Inject;
import dst.ass2.di.model.ScopeType;

@Component(scope = ScopeType.PROTOTYPE)
public class Container {
	@ComponentId
	public Long id;
	public Long timestamp = System.currentTimeMillis();
	@Inject
	public SimpleSingleton first;
	@Inject
	public SimpleSingleton second;
	@Inject
	public SimpleComponent component;
	@Inject
	public SimpleComponent anotherComponent;
}
