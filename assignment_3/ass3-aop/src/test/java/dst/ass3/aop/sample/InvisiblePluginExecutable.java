package dst.ass3.aop.sample;

import dst.ass3.aop.logging.Invisible;

public class InvisiblePluginExecutable extends AbstractPluginExecutable {
	@Override
	@Invisible
	public void execute() {
		super.execute();
	}
}
