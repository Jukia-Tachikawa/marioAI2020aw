package ch.idsia.agents.controllers;

import ch.idsia.agents.Agent;
import ch.idsia.agents.controllers.astar.GeneralizerLevelScene;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.engine.sprites.Sprite;
import ch.idsia.benchmark.mario.environments.Environment;

public class Task4_1Agent extends BasicMarioAIAgent implements Agent {
	
	private float lastPos = 0;
	private int movingDirection = 0;
	/*
	 * movingDirection = 1 -> Mario is moving to the right or not moving
	 * movingDirection = -1 -> Mario is moving to the left
	 */
	
	private boolean deadEnd;
	
	public Task4_1Agent() {
		super("Task4_1Agent");
		reset();
	}
	
	public void reset() {
		action = new boolean[Environment.numberOfKeys];
		action[Mario.KEY_SPEED] = true;
	}
	
	public boolean[] getAction() {
		
		float distanceTraveled = super.marioFloatPos[0] - lastPos;
		
		// checking Mario's direction
		if(distanceTraveled < 0) {
			movingDirection = -1;
		}else{
			movingDirection = 1;
		}
		lastPos = super.marioFloatPos[0];
		
		/*
		 * 1,2tick後に死ぬ可能性のある脅威に対処
		 * 
		 * 
		 */
		
		boolean isHoleNear;
		if(isHoleNear = super.levelScene[marioEgoRow+1][marioEgoCol+1*movingDirection] == 0) {
			for(int i = marioEgoRow+2; i < 19; i++) {
				if(super.levelScene[i][marioEgoCol+1*movingDirection] != 0) {
					isHoleNear = false;
					break;
				}
			}
		}
		
		boolean isHoleFar;
		if(isHoleFar = ! isHoleNear && super.levelScene[marioEgoRow+1][marioEgoCol+3*movingDirection] == 0) {
			for(int i = marioEgoRow+2; i < 19; i++) {
				if(super.levelScene[i][marioEgoCol+3*movingDirection] != 0) {
					isHoleFar = false;
					break;
				}
			}
		}
			
		if(super.levelScene[marioEgoRow+1][marioEgoCol+1*movingDirection] == 0 && super.enemies[marioEgoRow+1][marioEgoCol+1*movingDirection] == Sprite.KIND_GOOMBA) { //進行方向斜め上に敵
			setAction(false, false, false, true, isMarioAbleToShoot, false);
			 
			return action;
		}else if( //それ以外の周りに敵
			 (super.levelScene[marioEgoRow][marioEgoCol+1*movingDirection] == 0 && super.enemies[marioEgoRow][marioEgoCol+1*movingDirection] == Sprite.KIND_GOOMBA)
		   ||(super.levelScene[marioEgoRow][marioEgoCol+2*movingDirection] == 0 && super.enemies[marioEgoRow][marioEgoCol+2*movingDirection] == Sprite.KIND_GOOMBA)
		   ||(super.levelScene[marioEgoRow+1][marioEgoCol+2*movingDirection] == 0 && super.enemies[marioEgoRow+1][marioEgoCol+2*movingDirection] == Sprite.KIND_GOOMBA)
	       ||(super.levelScene[marioEgoRow][marioEgoCol-1*movingDirection] == 0 && super.enemies[marioEgoRow][marioEgoCol-1*movingDirection] == Sprite.KIND_GOOMBA)
		   ||(super.levelScene[marioEgoRow+1][marioEgoCol-1*movingDirection] == 0 && super.enemies[marioEgoRow+1][marioEgoCol-1*movingDirection] == Sprite.KIND_GOOMBA)
		        ) {
				
			if(isHoleFar) {
				setAction(false, false, false, isMarioAbleToJump, isMarioAbleToShoot, false);
				 
				return action;
			}else {
				if(movingDirection == 1) {
					setAction(true, false, false, isMarioAbleToJump, isMarioAbleToShoot, false);
					return action;
				}else {
					setAction(false, true, false, isMarioAbleToJump, isMarioAbleToShoot, false);
					return action;
				}
			}
		}else if(isHoleNear) {
			if(movingDirection == 1) {
				setAction(true, false, false, isMarioAbleToJump || ! isMarioOnGround, isMarioAbleToShoot, false);
				return action;
			}else {
				setAction(false, true, false, isMarioAbleToJump || ! isMarioOnGround, isMarioAbleToShoot, false);
				return action;
			}
		}
		
		/*
		 * 余裕があるときは前に進む
		 * 
		 */
		
		boolean isWallClimbable = false, isWallUnclimbable = false;
		if(super.levelScene[marioEgoRow][marioEgoCol+1] != 0) {
			for(int i = marioEgoRow + 1; i > 5; i--) {
				if(super.levelScene[i][marioEgoCol+1] == 0) {
					isWallClimbable = true;
					break;
				}
			}
			isWallUnclimbable = ! isWallClimbable;
		}
		
		if(isWallClimbable && isHoleFar) {
			if(distanceTraveled == 0) {
				setAction(true, false, false, isMarioAbleToJump || ! isMarioOnGround, false, false);
				return action;
			}else {
				setAction(false, false, false, false, false, false);
				return action;
			}
		}else if(isWallClimbable) {
			setAction(true, false, false, isMarioAbleToJump || ! isMarioOnGround, true, false);
			return action;
		}
		
		if(super.marioFloatPos[0] == 1590) {
			setAction(false, false, false, false, false, false);
			return action;
		}
		if(super.marioFloatPos[0] == 1595) {
			setAction(true, false, false, true, false, false);
			return action;
		}
		setAction(true, false, false, ! isMarioOnGround, false, false);
		return action;
					            
		
	}
	
	private void setAction(boolean right, boolean left, boolean down, boolean jump, boolean speed, boolean up) {
		action[Mario.KEY_RIGHT] = right;
		action[Mario.KEY_LEFT] = left;
		action[Mario.KEY_DOWN] = down;
		action[Mario.KEY_JUMP] = jump;
		action[Mario.KEY_SPEED] = speed;
		action[Mario.KEY_UP] = up;
	}

}
