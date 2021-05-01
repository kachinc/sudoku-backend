const Start = {
	template: `
	<div>
	
		<b-jumbotron header="Welcome to my sudoku game">

			<br>
			
			<b-button-group>
				<b-btn variant="success" class="btn-sm" to="/newgame">Start a new game</b-btn>
				<b-btn variant="info" class="btn-sm" to="/loadgame">Load saved game</b-btn>
			</b-button-group>
		
		</b-jumbotron>

	</div>
	`
			
}

const NewGame = {
	data: function () {
	    return {
	      diff: 0
	    }
	},
	computed: {
		diffMsg(){
			let clues = Math.round(81 - 81 * this.diff);
			let msg =  clues + ' clues.';
			if(clues < 17) {
				msg += ' Less than 17 clues.'
			} else if (clues == 81){
				msg += ' This will create a finished game.'
			}
			return msg;
		}
	},
	methods: {
		createNewGame(){
			router.push({ name: 'ingame', params: { diff: this.diff } });
		}
	},
	template: `
	<div>
	
		<b-card title="Pick a difficulty">
		<br>
		<div class="m-2">
			<b-form-input id="range-2" v-model="diff" type="range" min="0" max="1" step="0.01"></b-form-input>
			<span>Difficulty (0 to 1): {{ diff }}</span>
			<div class="alertMsg">{{diffMsg}}</div>
		</div>
		<div>
		<br>
			<b-button-group>
				<b-btn variant="success" class="btn-sm" @click="createNewGame()">Play</b-btn>
				<b-btn variant="danger" class="btn-sm" to="/">Back</b-btn>
			</b-button-group>
		</div>
		</b-card>
	</div>
	`		
}

const LoadGame = {
		data: function () {
		    return {
		      saveCode:'',
		      loading:false
		    }
		},
		computed: {
			
		},
		methods: {
			load(){
				let self = this;
				this.loading = true;
				axios.get('api/loadGame',{params:{uuidStr:this.saveCode}}).then(res => {
					console.log(res.data);
					if(res.data.uuidvalid){
						router.push({ name: 'ingame', params: {
								loadMode: true,
								diff: res.data.difficulty,
								boardStrOriginal: res.data.boardStrOriginal,
								boardStrNow: res.data.boardStrNow,
								elaspedTimeValue: res.data.elaspedTimeValue
							}
						});
					} else {
						this.$bvToast.toast('Save code is invalid.', {
							  toaster: 'b-toaster-top-left',
					          title: 'Oops!',
					          variant: 'danger'
					    });
					}
				}).catch(err => {
				    console.log(err);
				    self.showXhrError();
				}).then(() => {
					self.loading = false;
				});
			}
		},
		template: `
		<div>
			<b-overlay :show="loading" rounded="sm">
				<b-card title="Load saved game">
				<div class="m-2 py-2">
					<b-form-input v-model="saveCode" placeholder="Enter save code"></b-form-input>
				</div>
				<div>
					<b-button-group>
						<b-btn variant="info" class="btn-sm" @click="load()">Load</b-btn>
						<b-btn variant="danger" class="btn-sm" to="/">Back</b-btn>
					</b-button-group>
				</div>
				</b-card>
			</b-overlay>
		</div>
		`		
	}

const InGame = {
	data: function () {
		return {
			loadMode: this.$route.params.loadMode,
			diff: this.$route.params.diff,
			boardStrOriginal:'',
			board: [],
			boardDisabledFlag:[],
			pickCellValueCellIndex:0,
			loading:false,
			timerIntervalId:{},
			gameStartTime:{},
			elaspedTimeValue:0,
			elaspedTimeStr:'',
			saveCodeOut:'',
			timerPaused:false
		}
	},
	mounted (){
		if(this.loadMode){
			this.loadGameInit();
		} else {
			this.getNewGame();
		}
	},
	methods: {
		resetTimer(startTimeValue){
			clearInterval(this.timerIntervalId);
			this.elaspedTimeStr = '--:--';
			if(startTimeValue){
				this.gameStartTime = moment().subtract(moment(startTimeValue));
			} else {
				this.gameStartTime = moment();
			}
			this.timerIntervalId = setInterval(()=>{
				if(!this.timerPaused){
					let subtractedTime = moment().subtract(this.gameStartTime);
					this.elaspedTimeValue = subtractedTime.valueOf();
					this.elaspedTimeStr = subtractedTime.format('mm:ss');
				} else {
					this.gameStartTime = this.gameStartTime.add(1, 'seconds');
				}
			}, 1000);
		},
		loadGameInit(){
			this.boardStrOriginal = this.$route.params.boardStrOriginal;
			
			// set board with original str for disable flags
			this.setBoardByStr(this.boardStrOriginal);
			
			// set board with now str
			this.board = this.$route.params.boardStrNow.split("");
			this.board = this.board.map(e => e == '-' ? '': e);
			
			this.resetTimer(this.$route.params.elaspedTimeValue);
			
		},
		showXhrError(){
			this.$bvToast.toast('Server error occurred.', {
				  toaster: 'b-toaster-top-left',
		          title: 'Oops!',
		          variant: 'danger'
		    });
		},
		getNewGameBtn(){
			this.$bvModal.msgBoxConfirm('Do you want to quit the current game?').then(value=>{
				if(value === true){
					this.getNewGame();
				}
			});
		},
		backBtn(){
			this.$bvModal.msgBoxConfirm('Do you want to quit the current game?').then(value=>{
				if(value === true){
					this.$router.push('/newgame');
				}
			});
		},
		getNewGame(){
			let self = this;
			this.loading = true;
			this.timerPaused = false;
			axios.get('api/generateByDiff',{params:{diff:this.diff}}).then(res => {
				
				let str = res.data.substr(1);
				this.boardStrOriginal = str;
				self.setBoardByStr(str);
				
				this.resetTimer();
				
			}).catch(err => {
			    console.log(err);
			    self.showXhrError();
			}).then(() => {
				self.loading = false;
			});
		},
		validate(){		
			let self = this;
			self.loading = true;
			let str = this.getStrFromBoard();
			axios.get('api/validate',{params:{str:str}}).then(res => {
				if(res.data == true){
					self.$bvToast.toast('Congrats! The board is valid.', {
						  toaster: 'b-toaster-top-left',
				          title: 'Validation Result',
				          variant: 'success'
				    });
					//clearInterval(this.timerIntervalId); // stop timer
					this.timerPaused = true;
				} else {
					self.$bvToast.toast('Oops! The board is invalid.', {
						  toaster: 'b-toaster-top-left',
				          title: 'Validation Result',
				          variant: 'danger'
				    })
				}
			}).catch(err => {
			    console.log(err);
			    self.showXhrError();
			}).then(() => {
				self.loading = false;
			});
		},
		cellDisabled(i,j){
			return this.boardDisabledFlag[j + 9*i];
		},
		setBoardByStr(str){
			this.board = str.split("");		
			this.boardDisabledFlag = this.board.map(e => e == '-' ? false : true);
			this.board = this.board.map(e => e == '-' ? '': e);
		},
		getStrFromBoard(){
			let processedBoard = Array(81).fill().map((_, i) => this.board[i] ? this.board[i] : '-');
			return processedBoard.join('');
		},
		checkCellState(i,j){
			let regex = RegExp('^[1-9]$');
			return regex.test(this.board[j + 9*i]) ? null : false;
		},
		pickCellValue(i,j){
			this.pickCellValueCellIndex = j + 9*i;
			this.$bvModal.show('pick-cell-modal');
		},
		numPadPressed(num){
			this.board[this.pickCellValueCellIndex] = num;
			this.$bvModal.hide('pick-cell-modal');
			this.$forceUpdate();
		},
		saveGame(){
			let self = this;
			self.timerPaused = true;
			self.loading = true;
			let boardStrNow = this.getStrFromBoard();

			axios.get('api/saveGame',{params:{
						boardStrOriginal:this.boardStrOriginal,
						boardStrNow:boardStrNow,
						difficulty:this.diff,
						elaspedTimeValue:this.elaspedTimeValue
					}
			}).then(res => {
				this.saveCodeOut = res.data;
				this.$bvModal.show('save-code-modal');
			}).catch(err => {
			    console.log(err);
			    self.showXhrError();
			}).then(() => {
				self.loading = false;
			});
		}
	},
	watch:{
		timerPaused(val){
			if(val){
				
			}
		}
	},
	template: `
	<div>
		<b-overlay :show="loading" rounded="sm">

			<h5>Difficulty of this game: {{diff}}</h5>
			<h5>Elapsed Time <b-btn squared variant="outline-dark" size="sm" :pressed.sync="timerPaused"><b-icon icon="pause"></b-icon></b-btn> : <span v-bind:class="{blink_me:timerPaused}">{{elaspedTimeStr}}</span> </h5>
			

			
			
			<div class="board my-2">
				<b-aspect aspect="1">
				<table>
					<tr v-for="(n,i) in 9">
						<td  v-for="(m,j) in 9" style="text-align:center">
							<span v-if="cellDisabled(i,j)">{{board[j + 9*i]}}</span>
							<span @click="pickCellValue(i,j)" class="boardbtn" v-if="!cellDisabled(i,j)">
								<span v-if="board[j + 9*i] == ''">▧</span>
								<span v-else>{{board[j + 9*i]}}</span>
							</span>
						</td>
					</tr>
				</table>
				</b-aspect>
			</div>
			
			<div>
				<b-button-group>
					<b-btn variant="success"  class="btn-sm" @click="validate()">Check</b-btn>
					<b-btn variant="secondary" class="btn-sm" @click="getNewGameBtn()">New Game</b-btn>
					<b-btn variant="secondary" class="btn-sm" @click="saveGame()">Save</b-btn>
					<b-btn variant="secondary" class="btn-sm" target="_blank" rel="noopener noreferrer" :href="'api/sudokuPdf?' + 'str=' + boardStrOriginal + '&' + 'difficulty=' + diff ">PDF</b-btn>
					<b-btn variant="danger" class="btn-sm" @click="backBtn()">Back</b-btn>
				</b-button-group>
			</div>
		</b-overlay>
		
		<b-modal id="pick-cell-modal" hide-footer hide-header size="sm">
			<p id="numpadmsg">Touch outside to close</p>
			<b-button-group vertical>
			    <b-button-group>
			        <b-btn squared class="boardnumpadkey m-2" variant="outline-dark" @click="numPadPressed('1')">1</b-btn>
			        <b-btn squared class="boardnumpadkey m-2" variant="outline-dark" @click="numPadPressed('2')">2</b-btn>
			        <b-btn squared class="boardnumpadkey m-2" variant="outline-dark" @click="numPadPressed('3')">3</b-btn>
			    </b-button-group>
			    <b-button-group>
			        <b-btn squared class="boardnumpadkey m-2" variant="outline-dark" @click="numPadPressed('4')">4</b-btn>
			        <b-btn squared class="boardnumpadkey m-2" variant="outline-dark" @click="numPadPressed('5')">5</b-btn>
			        <b-btn squared class="boardnumpadkey m-2" variant="outline-dark" @click="numPadPressed('6')">6</b-btn>
			    </b-button-group>
			    <b-button-group>
			        <b-btn squared class="boardnumpadkey m-2" variant="outline-dark" @click="numPadPressed('7')">7</b-btn>
			        <b-btn squared class="boardnumpadkey m-2" variant="outline-dark" @click="numPadPressed('8')">8</b-btn>
			        <b-btn squared class="boardnumpadkey m-2" variant="outline-dark" @click="numPadPressed('9')">9</b-btn>
			    </b-button-group>
			    <b-button-group>
			        <b-btn squared class="boardnumpadkey m-2" variant="outline-danger" @click="numPadPressed('')">Clear</b-btn>
			    </b-button-group>
		    </b-button-group>
		</b-modal>
		
		<b-modal id="save-code-modal" title="Save Game" ok-only hide-header-close>
			The game has been saved. You may load it using this code:
			 <b-form-input v-model="saveCodeOut"></b-form-input>
		</b-modal>
		
	</div>
	`
				
	}

const routes = [
  { path: '/', component: Start },
  { path: '/newgame', component: NewGame },
  { path: '/loadgame', component: LoadGame },
  { name: 'ingame', path: '/ingame/:diff', component: InGame }
]

const router = new VueRouter({
  routes
})

const app = new Vue({
  router
}).$mount('#app')

window.onbeforeunload = function() {
  return "";
}