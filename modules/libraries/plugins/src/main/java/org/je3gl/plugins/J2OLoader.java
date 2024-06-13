/* Copyright (c) 2009-2024 jMonkeyEngine.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.je3gl.plugins;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.export.Savable;
import com.jme3.export.binary.BinaryImporter;
import com.jme3.system.JmeSystem;
import java.io.File;
import java.io.IOException;
import org.je3gl.plugins.asset.J2OKey;
import static org.je3gl.plugins.Debugger.*;

/**
 *
 * @author wil
 */
public class J2OLoader {

    private static AssetManager assetManager;
    
    public static void initialize(Application app) {
        J2OLoader.assetManager = app.getAssetManager();
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T load(J2OKey key) {
        if ("j2o".equals(key.getExtension())  || "J2O".equals(key.getExtension())) {
            BinaryImporter importer = BinaryImporter.getInstance();
            importer.setAssetManager(assetManager);
            
            try {
                String fullPath = File.separator + key.getName();                
                Savable obj = importer.load(JmeSystem.getResourceAsStream(fullPath));
                return (T) obj;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        apiGLLog("Extension " + key.getExtension() + " is not supported");
        return null;
    }
}
