package dst.ass2.di.type;

import dst.ass2.di.annotation.Component;
import dst.ass2.di.annotation.ComponentId;
import dst.ass2.di.model.ScopeType;

@Component(scope = ScopeType.PROTOTYPE)
public class SimpleComponent {
	@ComponentId
	public Long id;
}
