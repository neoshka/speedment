package com.speedment.runtime.field.method;

import com.speedment.runtime.annotation.Api;
import com.speedment.runtime.field.trait.HasByteValue;
import java.util.function.UnaryOperator;

/**
 * Represents a set-operation with all the metadata contained.
 * 
 * @param <ENTITY> entity type
 * @param <D>      database type
 * 
 * @author Emil Forslund
 * @since  3.0.0
 */
@Api(version = "3.0")
public interface SetToByte<ENTITY, D> extends UnaryOperator<ENTITY> {
    
    /**
     * Returns the field that this setter sets.
     * 
     * @return the field
     */
    HasByteValue<ENTITY, D> getField();
    
    /**
     * Returns the value that this setter will set in the field when it is
     * applied.
     * 
     * @return the field
     */
    byte getValue();
}