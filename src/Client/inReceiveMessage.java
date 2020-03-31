/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import Model.KMessage;
import java.io.IOException;

/**
 *
 */
public interface inReceiveMessage {
    
    public void ReceiveMessage(KMessage msg) throws IOException;
}
