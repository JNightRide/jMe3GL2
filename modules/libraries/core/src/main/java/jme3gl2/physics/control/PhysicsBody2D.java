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
package jme3gl2.physics.control;

import com.jme3.export.*;
import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.UserData;
import com.jme3.scene.control.Control;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jme3gl2.physics.PhysicsSpace;
import jme3gl2.util.Converter;

import org.dyn4j.Epsilon;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;

/**
 * An abstract implementation of the {@link jme3gl2.physics.control.PhysicsControl} 
 * interface.
 * <p>
 * An object of the <code>PhysicsBody2D</code> class is the body that the physics 
 * engine uses to give realism to the games. With this control we can manage a 
 * 2D model with or without physics.
 * 
 * @author wil
 * @version 1.5.5
 * @since 1.0.0
 */
public abstract class PhysicsBody2D extends Body implements Control, Savable, PhysicsControl<PhysicsBody2D> {
    /** Class logger. */
    private static final Logger LOGGER = Logger.getLogger(PhysicsBody2D.class.getName());
    
    /** Physical space. */
    protected PhysicsSpace<PhysicsBody2D> physicsSpace;
    /** The 2D model. */
    protected Spatial spatial;
        
    /**
     * Generates a new object of class <code>PhysicsBody2D</code> to generate a 
     * physical body from a 2D model.
     */
    public PhysicsBody2D() { }
    
    /**
     * (non-Javadoc)
     * @see jme3gl2.physics.control.PhysicsControl#setPhysicsSpace(jme3gl2.physics.PhysicsSpace) 
     * @param physicsSpace A {@link jme3gl2.physics.PhysicsSpace} object
     */
    @Override
    public void setPhysicsSpace(PhysicsSpace<PhysicsBody2D> physicsSpace) {
        this.physicsSpace = physicsSpace;
    }

    /**
     * (non-Javadoc)
     * @see jme3gl2.physics.control.PhysicsControl#getPhysicsSpace() 
     * @return A {@link jme3gl2.physics.PhysicsSpace} object
     */
    @Override
    public PhysicsSpace<PhysicsBody2D> getPhysicsSpace() {
        return physicsSpace;
    }

    /**
     * (non-Javadoc)
     * @param spatial object
     * @return object
     * @deprecated (?,?)
     */
    @Deprecated
    @Override
    public Control cloneForSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * (non-Javadoc)
     * @see com.jme3.scene.control.Control#setSpatial(com.jme3.scene.Spatial) 
     * @param spatial object
     */
    @Override
    public void setSpatial(Spatial spatial) {
        if (this.spatial != null && spatial != null && spatial != this.spatial) {
            throw new IllegalStateException("This control has already been added to a Spatial");
        }
        this.spatial = spatial;
        
        this.applyPhysicsLocation(this);
        this.applyPhysicsRotation(this);
        this.ready();
    }

    /**
     * (non-Javadoc)
     * @see com.jme3.scene.control.Control#update(float) 
     * @param tpf float
     */
    @Override
    public void update(float tpf) {
        if (!isEnabled())
            return;        
        controlUpdate(tpf);
        physicsProcess(tpf);
    }

    /**
     * (non-Javadoc)
     *  @see com.jme3.scene.control.Control#render(com.jme3.renderer.RenderManager, com.jme3.renderer.ViewPort) 
     * @param rm render-manager
     * @param vp view-port
     */
    @Override
    public void render(RenderManager rm, ViewPort vp) {
        if (!isEnabled())
            return;
        controlRender(rm, vp);
    }

    /**
     * Returns the {@link com.jme3.scene.Spatial} assigned to this physical body.
     * @param <T> spatial type
     * @return A {@link com.jme3.scene.Spatial} object
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Spatial> T getJmeObject() {
        return (T) spatial;
    }

    /** Data initialization for this body (optional). */
    protected void ready() {}
    
    /**
     * Updating physical processes (optional).
     * @param delta time per frame (in seconds)
     */
    protected void physicsProcess(float delta) {}
    
    /**
     * To be implemented in subclass.
     * @param tpf time per frame (in seconds)
     */
    protected abstract void controlUpdate(float tpf);

    /**
     * To be implemented in subclass.
     * @param rm the RenderManager rendering the controlled Spatial (not null)
     * @param vp the ViewPort being rendered (not null)
     */
    protected abstract void controlRender(RenderManager rm, ViewPort vp);
    
    /**
     * (non-Javadoc)
     * @see com.jme3.export.Savable#read(com.jme3.export.JmeImporter) 
     * 
     * @param im {@link com.jme3.export.JmeImporter}
     * @throws IOException throws
     */
    @Override
    public void read(JmeImporter im) throws IOException { 
        InputCapsule in = im.getCapsule(this);
        UserData userObject = (UserData) in.readSavable("UserData", null);
        if (userObject != null) {
            setUserData(userObject.getValue());
        }
        
        int fSize = in.readInt("BodyFixture#fSize", 0);
        for (int j = 0; j < fSize; j++) {
            PhysicsFixture pf = (PhysicsFixture) in.readSavable("BodyFixture#" + String.valueOf(j), null);
            addFixture(pf.getFixture());
        }
        
        rotate(in.readDouble("RotationAngle", 0));
        translate(Converter.toVector2ValueOfDyn4j((Vector2f) in.readSavable("Translation", new Vector2f())));
        setLinearVelocity(Converter.toVector2ValueOfDyn4j((Vector2f) in.readSavable("LinearVelocity", new Vector2f())));
        setAngularVelocity(in.readDouble("AngularVelocity", 0));
        
        setEnabled(in.readBoolean("Enabled", true));
        setAtRest(in.readBoolean("AtRest", false));
        setAtRestDetectionEnabled(in.readBoolean("AtRestDetectionEnabled", true));
        setBullet(in.readBoolean("Bullet", false));
        
        setLinearDamping(in.readDouble("LinearDamping", Body.DEFAULT_LINEAR_DAMPING));
        setAngularDamping(in.readDouble("AngularDamping", Body.DEFAULT_ANGULAR_DAMPING));
        setGravityScale(in.readDouble("GravityScale", 1.0));
        
        Vector2 mCenter = Converter.toVector2ValueOfDyn4j((Vector2f) in.readSavable("Center", new Vector2f(0.0F, 0.0F)));
        Mass myMass     = new Mass(mCenter, in.readDouble("Mass", 0.0), in.readDouble("Inertia", 0.0));
        myMass.setType(in.readEnum("MassType", MassType.class, MassType.INFINITE));
        
        applyForce(Converter.toVector2ValueOfDyn4j((Vector2f) in.readSavable("AccumulatedForce", new Vector2f(0.0F, 0.0F))));
        applyTorque(in.readDouble("AccumulatedTorque", 0));
        
        spatial    = (Spatial) in.readSavable("Spatial", null);        
        if (spatial == null) {
            LOGGER.log(Level.SEVERE, "There is NO 'Spatial' to represent this physical body");
        }
    }
    
    /**
     * (non-Javadoc)
     * @see com.jme3.export.Savable#write(com.jme3.export.JmeExporter) 
     * 
     * @param ex {@link com.jme3.export.JmeExporter}
     * @throws IOException throws
     */
    @Override
    @SuppressWarnings("unchecked")
    public void write(JmeExporter ex) throws IOException { 
        OutputCapsule out = ex.getCapsule(this);      
        Object userObject = this.getUserData();
        byte userType = -1;
        try {
            userType = UserData.getObjectType(userObject);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unsupported type: {0}", userObject == null ? null : userObject.getClass().getName());
        }
        
        if (userType != -1) {
            out.write(new UserData(userType, userObject), "UserData", null);
        }
        
        int fSize = this.getFixtureCount();
        out.write(fSize, "BodyFixture#fSize", 0);
        
        for (int j = 0; j < fSize; j++) {
            BodyFixture bf  = this.getFixture(j);            
            out.write(new PhysicsFixture(bf), "BodyFixture#" + String.valueOf(j), null);
        }
        
        // set the transform
        if (Math.abs(getTransform().getRotationAngle()) > Epsilon.E) {
            out.write(getTransform().getRotationAngle(), "RotationAngle", 0);
        }
        if (!getTransform().getTranslation().isZero()) {
            out.write(Converter.toVector2fValueOfJME3(getTransform().getTranslation()), "Translation", null);
        }
        // set velocity
        if (!getLinearVelocity().isZero()) {
            out.write(Converter.toVector2fValueOfJME3(getLinearVelocity()), "LinearVelocity", null);
        }
        if (Math.abs(getAngularVelocity()) > Epsilon.E) {
            out.write(getAngularVelocity(), "AngularVelocity", 0);
        }
        // set state properties
        if (!isEnabled()) {
            out.write(isEnabled(), "Enabled", true);
        } // by default the body is active
        if (isAtRest()) {
            out.write(isAtRest(), "AtRest", false);
        } // by default the body is awake
        if (!isAtRestDetectionEnabled()) {
            out.write(isAtRestDetectionEnabled(), "AtRestDetectionEnabled", true);
        } // by default auto sleeping is true
        if (isBullet()) {
            out.write(isBullet(), "Bullet", false);
        } // by default the body is not a bullet
        // set damping
        if (getLinearDamping() != Body.DEFAULT_LINEAR_DAMPING) {
            out.write(getLinearDamping(), "LinearDamping", 0);
        }
        if (getAngularDamping() != Body.DEFAULT_ANGULAR_DAMPING) {
            out.write(getAngularDamping(), "AngularDamping", 0);
        }
        // set gravity scale
        if (getGravityScale() != 1.0) {
            out.write(getGravityScale(), "GravityScale", 0);
        }
        
        // set mass properties last
        Mass myMass = getMass();
        out.write(Converter.toVector2fValueOfJME3(myMass.getCenter()), "Center", null);
        out.write(myMass.getMass(), "Mass", 0);
        out.write(myMass.getInertia(), "Inertia", 0);
        out.write(myMass.getType(), "MassType", MassType.INFINITE);
        
        // set force/torque accumulators
        if (!getAccumulatedForce().isZero()) {
            out.write(Converter.toVector2fValueOfJME3(getAccumulatedForce()), "AccumulatedForce", null);
        }
        if (Math.abs(getAccumulatedTorque()) > Epsilon.E) {
            out.write(getAccumulatedTorque(), "AccumulatedTorque", 0);
        }
        
        // jme3
        out.write(spatial, "Spatial", null);
    }
}