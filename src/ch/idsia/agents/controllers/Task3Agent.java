/*
 * Copyright (c) 2009-2010, Sergey Karakovskiy and Julian Togelius
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Mario AI nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package ch.idsia.agents.controllers;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.mario.engine.sprites.Sprite;
import ch.idsia.benchmark.mario.engine.GeneralizerLevelScene;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy, sergey.karakovskiy@gmail.com
 * Date: Apr 8, 2009
 * Time: 4:03:46 AM
 */

public class Task3Agent extends BasicMarioAIAgent implements Agent
{
	int trueJumpCounter = 0;
	int trueSpeedCounter = 0;

	public Task3Agent()
	{
		super("Task3Agent");
		reset();
	}

	public void reset()
	{
		action = new boolean[Environment.numberOfKeys];
		action[Mario.KEY_RIGHT] = true;
	}

	public boolean isObstacle(int r, int c){
		return getReceptiveFieldCellValue(r, c)==GeneralizerLevelScene.BRICK
				|| getReceptiveFieldCellValue(r, c)==GeneralizerLevelScene.BORDER_CANNOT_PASS_THROUGH
				|| getReceptiveFieldCellValue(r, c)==GeneralizerLevelScene.FLOWER_POT_OR_CANNON
				|| getReceptiveFieldCellValue(r, c)==GeneralizerLevelScene.LADDER;
	}
	
	public boolean isHole(int c) {//縦の列cの位置に穴があればtrue, なければfalseを返す
		for(int r = marioEgoRow; r <= 18; r++) {
			if(getReceptiveFieldCellValue(r,c) != 0) {//縦の列cにおいてマリオより下に障害物があれば	                                           *
				return false;                         //falseを返す
			}
		}
		return true;
	}

	public boolean isEnemy(int r, int c){//(r, c)に敵がいればtrue
		return getEnemiesCellValue(r, c) != Sprite.KIND_NONE; 
	}
	
	public boolean isEnemy(int c) {//縦の列cの位置に敵がいればtrue
		for(int r = 0; r <= 18; r++) {
			if(isEnemy(r, c)) {
				return true;
			}
		}
		return false;
	}

	public boolean[] getAction()
	{
		action[Mario.KEY_RIGHT] = true;
		action[Mario.KEY_SPEED] = false;
		boolean jumpOrNot = isMarioAbleToJump || !isMarioOnGround;
		
		if(isObstacle(marioEgoRow, marioEgoCol + 1)){//目の前に障害物があったらジャンプ
			action[Mario.KEY_JUMP] = jumpOrNot;
		}
		
		if(isHole(marioEgoCol + 1)) {//目の前に穴があったらジャンプ
			action[Mario.KEY_JUMP] = jumpOrNot;
		}

		//敵に対する処理
		if(isMarioAbleToShoot){//マリオがファイアの時
			if(isEnemy(marioEgoRow, marioEgoCol + 1)){
				action[Mario.KEY_JUMP] = jumpOrNot;
				action[Mario.KEY_RIGHT] = false;
			}else if(isEnemy(marioEgoRow, marioEgoCol + 2) || isEnemy(marioEgoCol + 3)){
				action[Mario.KEY_SPEED] = true;
				action[Mario.KEY_RIGHT] = false;
			}else if (isEnemy(marioEgoCol + 4) || isEnemy(marioEgoCol + 5)) {
				action[Mario.KEY_SPEED] = true;
			}
		}else{//マリオがファイアでないとき
			if(isEnemy(marioEgoRow, marioEgoCol + 1) || isEnemy(marioEgoRow, marioEgoCol + 2)) {
				action[Mario.KEY_JUMP] = jumpOrNot;
				action[Mario.KEY_RIGHT] = false;
			}
		}
		return action;
	}
}