package dst.ass2.di.type;

import dst.ass2.di.annotation.Component;
import dst.ass2.di.annotation.ComponentId;
import dst.ass2.di.model.ScopeType;

@Component(scope = ScopeType.SINGLETON)
public class SimpleSingleton {
	@ComponentId
	public Long id;
}
