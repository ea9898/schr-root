package moscow.ptnl.app.trace;

public class LoggerDataHolder {
	
	private static final InheritableThreadLocal<moscow.ptnl.app.trace.TreeCallStackLogger> holder = new InheritableThreadLocal<>();

	public static moscow.ptnl.app.trace.TreeCallStackLogger startRequest() {
		holder.set(new moscow.ptnl.app.trace.TreeCallStackLogger());
		return holder.get();
	}

	public static moscow.ptnl.app.trace.TreeCallStackLogger getCurrentRequest() {
		return holder.get();
	}

	public static void endRequest() {
		holder.set(null);
	}
}
