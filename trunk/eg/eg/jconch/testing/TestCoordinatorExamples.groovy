
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.util.ArrayList
import java.util.List
import java.util.concurrent.TimeUnit

import javax.swing.SwingUtilities

/**
 * Examples of how to use the TestCoordinator object. 
 */

import jconch.testing.TestCoordinator

TestCoordinator coord = new TestCoordinator()

MyComponent component = new MyComponent()
component.addActionListener({ ActionEvent e -> 
        assert e.source != null
        assert "click" == e.actionCommand
        coord.finishTest()
    } as ActionListener) 

component.click()
coord.delayTestFinish()

component.click()
coord.delayTestFinish(1000)

component.click()
coord.delayTestFinish(1, TimeUnit.SECONDS)

println 'Success'

/**
 * Mock component to spawn off click events on a 2nd thread. 
 */
class MyComponent {

    private def listeners = []

    public void addActionListener(ActionListener listener) {
        listeners << listener
    }

    public void click() {
        SwingUtilities.invokeLater {
            listeners.each { listener ->
                def event = new ActionEvent(new Object(), 24987234, "click");
                listener.actionPerformed(event);
            }
        }    
    }
}
