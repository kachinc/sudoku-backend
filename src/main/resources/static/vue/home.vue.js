const Start = {
	template: `
	<div>
	
		<b-jumbotron header="Welcome to this Sudoku Game" lead="by Johnson">
			<p>Here you may play a sudoku game with a difficulty you choose, you may even download the game as a PDF file!</p>
			<br>
			
			<b-button-group>
				<b-btn size="lg" variant="success" to="/newgame">Start a new game</b-btn>
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
		<h3>Select difficulty</h3>
		<div class="m-2">
			<b-form-input id="range-2" v-model="diff" type="range" min="0" max="1" step="0.01"></b-form-input>
			<span>Difficulty (0 to 1): {{ diff }}</span>
			<div class="alertMsg">{{diffMsg}}</div>
		</div>
		<div>
			<b-button-group>
				<b-btn size="lg" variant="success" @click="createNewGame()">Create New Game</b-btn>
				<b-btn size="lg" variant="danger" to="/">Back</b-btn>
			</b-button-group>
		</div>
	</div>
	`		
}

const InGame = {
	data: function () {
		return {
			diff: this.$route.params.diff,
			boardStrOriginal:'',
			board: [],
			boardDisabledFlag:[],
			pickCellValueCellIndex:0,
			loading:false,
			timerIntervalId:{},
			gameStartTime:{},
			elaspedTimeStr:''
		}
	},
	mounted (){
		this.getNewGame();
		console.log('mounted')		
	},
	methods: {
		showXhrError(){
			this.$bvToast.toast('Server error occurred.', {
				  toaster: 'b-toaster-top-left',
		          title: 'Oops!',
		          variant: 'danger'
		    });
		},
		getNewGameBtn(){
			this.$bvModal.msgBoxConfirm('Do you want to play a new game? Your progress will be lost.').then(value=>{
				if(value === true){
					this.getNewGame();
				}
			});
		},
		backBtn(){
			this.$bvModal.msgBoxConfirm('Do you want to go to the previous page? Your progress will be lost.').then(value=>{
				if(value === true){
					this.$router.push('/newgame');
				}
			});
		},
		getNewGame(){
			let self = this;
			this.loading = true;
			axios.get('/api/generateByDiff',{params:{diff:this.diff}}).then(res => {
				
				let str = res.data.substr(1);
				this.boardStrOriginal = str;
				self.setBoardByStr(str);
				
				// reset timer
				clearInterval(this.timerIntervalId);
				this.elaspedTimeStr = '00:00';
				this.gameStartTime = moment();
				this.timerIntervalId = setInterval(()=>{
					this.elaspedTimeStr = moment().subtract(this.gameStartTime).format('mm:ss');
				}, 1000);
				
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
			axios.get('/api/validate',{params:{str:str}}).then(res => {
				if(res.data == true){
					self.$bvToast.toast('Congrats! The board is valid.', {
						  toaster: 'b-toaster-top-left',
				          title: 'Validation Result',
				          variant: 'success'
				    });
					clearInterval(this.timerIntervalId); // stop timer
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
		}
	},
	template: `
	<div>
		<b-overlay :show="loading" rounded="sm">
			<p>
			<h3>Difficulty of this game: {{diff}}</h3>
			<h3>Elapsed Time: {{elaspedTimeStr}}</h3>
			
			</p>
			
			
			<div class="board my-2">
				<b-aspect aspect="1">
				<table>
					<tr v-for="(n,i) in 9">
						<td  v-for="(m,j) in 9" style="text-align:center">
							<span v-if="cellDisabled(i,j)">{{board[j + 9*i]}}</span>
							<b-btn @click="pickCellValue(i,j)" variant="secondary" class="boardbtn" v-if="!cellDisabled(i,j)">{{board[j + 9*i]}}</b-btn>
						</td>
					</tr>
				</table>
				</b-aspect>
			</div>
			
			<div>
				<b-button-group>
					<b-btn variant="success" size="lg" @click="validate()">Validate</b-btn>
					<b-btn variant="primary" size="lg" @click="getNewGameBtn()">New Game</b-btn>
					<b-btn variant="dark" size="lg" :href="'api/sudokuPdf?' + 'str=' + boardStrOriginal + '&' + 'difficulty=' + diff ">Download PDF</b-btn>
					<b-btn size="lg" variant="danger" @click="backBtn()">Back</b-btn>
				</b-button-group>
			</div>
		</b-overlay>
		
		<b-modal id="pick-cell-modal" hide-footer hide-header size="sm">
			<b-button-group vertical >
			    <b-button-group>
			        <b-btn squared class="boardnumpadkey" variant="outline-dark" @click="numPadPressed('1')">1</b-btn>
			        <b-btn squared class="boardnumpadkey" variant="outline-dark" @click="numPadPressed('2')">2</b-btn>
			        <b-btn squared class="boardnumpadkey" variant="outline-dark" @click="numPadPressed('3')">3</b-btn>
			    </b-button-group>
			    <b-button-group>
			        <b-btn squared class="boardnumpadkey" variant="outline-dark" @click="numPadPressed('4')">4</b-btn>
			        <b-btn squared class="boardnumpadkey" variant="outline-dark" @click="numPadPressed('5')">5</b-btn>
			        <b-btn squared class="boardnumpadkey" variant="outline-dark" @click="numPadPressed('6')">6</b-btn>
			    </b-button-group>
			    <b-button-group>
			        <b-btn squared class="boardnumpadkey" variant="outline-dark" @click="numPadPressed('7')">7</b-btn>
			        <b-btn squared class="boardnumpadkey" variant="outline-dark" @click="numPadPressed('8')">8</b-btn>
			        <b-btn squared class="boardnumpadkey" variant="outline-dark" @click="numPadPressed('9')">9</b-btn>
			    </b-button-group>
			    <b-button-group>
			        <b-btn squared class="boardnumpadkey" variant="outline-danger" @click="numPadPressed('')">Clear</b-btn>
			    </b-button-group>
		    </b-button-group>
		</b-modal>
		
	</div>
	`
				
	}

const routes = [
  { path: '/', component: Start },
  { path: '/newgame', component: NewGame },
  { name: 'ingame', path: '/ingame/:diff', component: InGame }
]

const router = new VueRouter({
  routes
})

const app = new Vue({
  router
}).$mount('#app')
