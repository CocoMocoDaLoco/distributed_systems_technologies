package dst.ass2.di.type;

import dst.ass2.di.annotation.Component;
import dst.ass2.di.annotation.ComponentId;
import dst.ass2.di.annotation.Inject;
import dst.ass2.di.model.ScopeType;

@Component(scope = ScopeType.PROTOTYPE)
public class ComplexComponent {
	@ComponentId
	public Long id;

	@Inject(required = false)
	public Void theVoid;
	@Inject(required = false)
	public Invalid invalid;

	@Inject(required = true)
	public SimpleSingleton singleton;
	@Inject(specificType = SimpleSingleton.class)
	public Object unknownSingleton;

	@Inject(specificType = SimpleSingleton.class, required = false)
	public SimpleComponent singletonPrototype;
}
