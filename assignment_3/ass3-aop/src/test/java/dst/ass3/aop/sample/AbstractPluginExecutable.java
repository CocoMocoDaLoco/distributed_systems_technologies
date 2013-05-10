package dst.ass3.aop.sample;

import dst.ass3.aop.IPluginExecutable;
import dst.ass3.aop.event.EventBus;
import dst.ass3.aop.event.EventType;
import org.springframework.aop.support.AopUtils;

public abstract class AbstractPluginExecutable implements IPluginExecutable {
	@Override
	public void execute() {
		EventBus eventBus = EventBus.getInstance();
		eventBus.add(EventType.PLUGIN_START, this, AopUtils.getTargetClass(this).getSimpleName() + " is executed!");

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// Should not happen but is not critical so the stack trace is printed to grab some attention ;-)
			e.printStackTrace();
		}

		eventBus.add(EventType.PLUGIN_END, this, AopUtils.getTargetClass(this).getSimpleName() + " is finished!");
	}

	@Override
	public void interrupted() {
	}
}
