package net.kgtkr.seekprog;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Map;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.ClassType;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Location;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.PathSearchingVirtualMachine;
import com.sun.jdi.StackFrame;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.StepRequest;

public class App {

	public static void main(String[] args) throws Exception {
		Class classToDebug = Class.forName("Pde");
		LaunchingConnector launchingConnector = Bootstrap.virtualMachineManager().defaultConnector();
		Map<String, Connector.Argument> env = launchingConnector.defaultArguments();
		env.get("main").setValue(classToDebug.getName());
		env.get("options").setValue("-classpath " + System.getProperty("java.class.path"));
		VirtualMachine vm = launchingConnector.launch(env);
		ClassPrepareRequest classPrepareRequest = vm.eventRequestManager().createClassPrepareRequest();
		classPrepareRequest.addClassFilter(classToDebug.getName());
		classPrepareRequest.enable();

		MethodEntryRequest methodEntryRequest = vm.eventRequestManager().createMethodEntryRequest();
		methodEntryRequest.addClassFilter(classToDebug.getName());
		methodEntryRequest.enable();

		ObjectReference gBak = null;

		EventSet eventSet = null;

		try {
			while ((eventSet = vm.eventQueue().remove()) != null) {
				for (Event event : eventSet) {
					if (event instanceof ClassPrepareEvent) {
						ClassPrepareEvent evt = (ClassPrepareEvent) event;
						ClassType classType = (ClassType) evt.referenceType();
					}

					if (event instanceof MethodEntryEvent) {
						MethodEntryEvent evt = (MethodEntryEvent) event;
                        if (evt.method().name().equals("draw")) {
							StackFrame frame = evt.thread().frame(0);
							ObjectReference instance = frame.thisObject();
                            IntegerValue frameCount = (IntegerValue) instance.getValue(instance.referenceType().fieldByName("frameCount"));

							ObjectReference surface = (ObjectReference) instance.getValue(instance.referenceType().fieldByName("surface"));
							System.out.println(frameCount.intValue());
							if (frameCount.intValue() == 1) {
								surface.setValue(surface.referenceType().fieldByName("frameRatePeriod"), vm.mirrorOf(1));
								gBak = (ObjectReference) instance.getValue(instance.referenceType().fieldByName("g"));
								gBak.disableCollection();
								ClassType PGraphicsClassType = (ClassType) vm.classesByName("processing.core.PGraphics").get(0);
								instance.setValue(
									instance.referenceType().fieldByName("g"),
									PGraphicsClassType.newInstance(evt.thread(), PGraphicsClassType.methodsByName("<init>", "()V").get(0),
									new ArrayList<Value>(),
									0)
								);
							}
							if (frameCount.intValue() == 6000) {
								surface.setValue(surface.referenceType().fieldByName("frameRatePeriod"), vm.mirrorOf(16666666L));
								instance.setValue(instance.referenceType().fieldByName("g"), gBak);
								gBak.enableCollection();
							}
                        }
					}

                    if (event instanceof VMDisconnectEvent) {
                        System.out.println("VM is now disconnected.");
                        return;
                    }
				}
				
				{
					InputStreamReader reader = new InputStreamReader(vm.process().getInputStream());
					OutputStreamWriter writer = new OutputStreamWriter(System.out);
					int size = 0;
					char[] buf = new char[1024];
					while (reader.ready() && (size = reader.read(buf)) != -1) {
						writer.write(buf, 0, size);
					}
					writer.flush();
				}

				{
					InputStreamReader reader = new InputStreamReader(vm.process().getErrorStream());
					OutputStreamWriter writer = new OutputStreamWriter(System.err);
					int size = 0;
					char[] buf = new char[1024];
					while (reader.ready() && (size = reader.read(buf)) != -1) {
						writer.write(buf, 0, size);
					}
					writer.flush();
				}
				
				vm.resume();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			InputStreamReader reader = new InputStreamReader(vm.process().getInputStream());
			OutputStreamWriter writer = new OutputStreamWriter(System.out);
			int size = 0;
			char[] buf = new char[1024];
			while ((size = reader.read(buf)) != -1) {
				writer.write(buf, 0, size);
			}
			writer.flush();
		}

	}

}
