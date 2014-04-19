package com.cmov.bomberman.model;

/**
 * Created by JoãoEduardo on 15-04-2014.
 */

/**
 * This class allows to create algorithms or even to control any object that
 * depends on this interface.
 * Ex: Bomberman, Robot
 */
public interface Algorithm {
	public String getNextActionName();
}
