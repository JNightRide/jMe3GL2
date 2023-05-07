/* Copyright (c) 2009-2023 jMonkeyEngine.
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
package jMe3GL2.renderer;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;

import jMe3GL2.physics.control.PhysicsBody2D;
import jMe3GL2.physics.control.RigidBody2D;
import jMe3GL2.scene.TilesHeet;
import jMe3GL2.scene.shape.Sprite;
import jMe3GL2.util.jMe3GL2Utils;

import org.dyn4j.geometry.MassType;

/**
 * Un <code>DefaultTileMapRender</code> es una clase encargado de implementar la
 * interfaz <code>TileMapRender</code> con las funciones predeterminadas.
 * 
 * @author wil
 * @version 1.0-SNAPSHOT
 * 
 * @since 1.5.0
 */
public class DefaultTileMapRender implements TileMapRender {

    /**
     * (non-JavaDoc)
     * @see TileMapRender#render(jMe3GL2.scene.TilesHeet.Tile, jMe3GL2.scene.TilesHeet, com.jme3.asset.AssetManager) 
     * 
     * @param tile {@link TilesHeet.Tile}
     * @param tilesHeet {@link TilesHeet}
     * @param assetManager {@code AssetManager}
     * @return {@code Geometry}
     */
    @Override
    public Geometry render(TilesHeet.Tile tile, TilesHeet tilesHeet, AssetManager assetManager) {
        TilesHeet.Flot position = tile.getPosition(),
                       quadrant = tilesHeet.getQuadrant();
        
        TilesHeet.Int location     = tile.getLocation(),
                      quadrantSize = tilesHeet.getQuadrantSize();
        
        final Sprite sprite = new Sprite(quadrant.getA(),     quadrant.getB(), 
                                         quadrantSize.getA(), quadrantSize.getB(), 
                                         location.getA(),     location.getB());
        
        final Material mat = jMe3GL2Utils.loadMaterial(assetManager, tilesHeet.getTexture().getName());
        mat.setFloat("AlphaDiscardThreshold", 0.0F);
        
        final Geometry geom = new Geometry(tile.getId(), sprite);
        geom.setMaterial(mat);
        geom.setQueueBucket(RenderQueue.Bucket.Transparent);

        RigidBody2D rbd = new RigidBody2D();
        rbd.setMass(MassType.INFINITE);
        rbd.translate(position.getA(), position.getB());
        
        geom.addControl(rbd);
        return geom;
    }

    /**
     * (non-JavaDoc)
     * @see TileMapRender#update(com.jme3.scene.Geometry, jMe3GL2.scene.TilesHeet.Tile, jMe3GL2.scene.TilesHeet, com.jme3.asset.AssetManager) 
     * 
     * @param geo {@code Geometry}
     * @param tile {@link TilesHeet.Tile}
     * @param tilesHeet {@link TilesHeet}
     * @param assetManager {@code AssetManager}
     */
    @Override
    public void update(Geometry geo, TilesHeet.Tile tile, TilesHeet tilesHeet, AssetManager assetManager) {
        Mesh mesh = geo.getMesh();
        if (!(mesh instanceof Sprite)) {
            throw new UnsupportedOperationException("Not supported yet [" + mesh + "].");
        }
        
        TilesHeet.Flot position = tile.getPosition(),
                       quadrant = tilesHeet.getQuadrant();
        
        TilesHeet.Int location  = tile.getLocation();
        
        ((Sprite) mesh).updateTextureCoords(location.getA(), location.getB());
        ((Sprite) mesh).updateVertexSize(quadrant.getA(), quadrant.getB());
        geo.setMesh(mesh);
        
        PhysicsBody2D rbd = geo.getControl(PhysicsBody2D.class);
        if (rbd != null) {
            rbd.getTransform().setTranslation(position.getA(), position.getB());
        }
    }
}