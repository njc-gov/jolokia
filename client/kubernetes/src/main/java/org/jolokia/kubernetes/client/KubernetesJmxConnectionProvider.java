package org.jolokia.kubernetes.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorProvider;
import javax.management.remote.JMXServiceURL;
import org.jolokia.client.jmxadapter.JolokiaJmxConnector;

/**
 * I provide support for handling JMX urls over the Jolokia protocol to JVMs running in kubernetes pods
 * Syntax examples
 * <ul>
 *   <li>service:jmx:kubernetes:///api/v1/namespaces/mynamespace/pods/mypodname-.+/actuator/jolokia/</li>
 *   <li>service:jmx:kubernetes:///api/v1/namespaces/mynamespace/services/myservice-.+/actuator/jolokia/</li>
 * </ul>
 *
 * Regular expressions in service url is supported so you can have working URLs across deploys.
 * Regular expression URLs will connect to the first pod/service that matches expession.
 * Prerequesite: You should have <code>kubectl</code> installed and have valid credentiatls for k8s cluster
 * readily stored under $HOME/.kube/config
 * My Jar contains a service loader, so that Jolokia JMX protocol is supported
 * as long as my jar (jmx-adapter-version-standalone.jar) is on the classpath
 *
 * <pre>
 *   Example:
 *   //NB: include trailing slash to jolokia endpoint
 *   JMXConnector connector = JMXConnectorFactory
 *             .connect(new JMXServiceURL("service:jmx:kubernetes:///api/v1/namespaces/mynamespace/pods/mypodname-.+/actuator/jolokia/")));
 *         connector.connect();
 *         connector.getMBeanServerConnection();
 *
 * </pre>
 */
public class KubernetesJmxConnectionProvider implements JMXConnectorProvider {
    @Override
    public JMXConnector newJMXConnector(JMXServiceURL serviceURL, Map<String, ?> environment) throws IOException {
        //the exception will be handled by JMXConnectorFactory so that other handlers are allowed to handle
        //other protocols
        if(!"kubernetes".equals(serviceURL.getProtocol())) {
            throw new MalformedURLException(String.format("Invalid URL %s : Only protocol \"kubernetes\" is supported (not %s)",  serviceURL, serviceURL.getProtocol()));
        }
        return new KubernetesJmxConnector(serviceURL, environment);
    }
}
