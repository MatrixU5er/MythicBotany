package mythicbotany.network;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

import java.lang.reflect.InvocationTargetException;

final class ClientNetworkDispatch {

    private static final String HANDLER_CLASS = "mythicbotany.network.ClientNetworkHandlers";

    private ClientNetworkDispatch() {
    }

    static void dispatch(String method, Object message) {
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return;
        }
        try {
            Class<?> handler = Class.forName(HANDLER_CLASS, true, ClientNetworkDispatch.class.getClassLoader());
            handler.getMethod(method, message.getClass()).invoke(null, message);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to dispatch client network message " + method, unwrap(e));
        }
    }

    private static Throwable unwrap(ReflectiveOperationException e) {
        return e instanceof InvocationTargetException invocation && invocation.getCause() != null
                ? invocation.getCause() : e;
    }
}
