const Start = {
	template: `
	<div>
		
		<router-link to="/newgame"><b-btn>Start a new game</b-btn></router-link>
		
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
			<b-btn variant="success" @click="createNewGame()">Create New Game</b-btn>
			<router-link to="/"><b-btn variant="danger">Back</b-btn></router-link>
		</div>
	</div>
	`		
}

const InGame = {
	data: function () {
		return {
			diff: this.$route.params.diff,
			board: [],
			boardDisabledFlag:[],
			pickCellValueCellIndex:0,
			loading:false
		}
	},
	mounted (){
		this.getNewGame();
	},
	methods: {
		showXhrError(){
			this.$bvToast.toast('Server error occurred.', {
				  toaster: 'b-toaster-top-left',
		          title: 'Oops!',
		          variant: 'danger'
		    });
		},
		getNewGame(){
			let self = this;
			this.loading = true;
			axios.get('/api/generateByDiff',{params:{diff:this.diff}}).then(res => {
				let str = res.data.substr(1);
				self.setBoardByStr(str);
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
				    })
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
			<b-alert show variant="danger">The problem generated is incorrect (repeats in the subareas)</b-alert>
			</p>
			
			
			<div class="board my-2">
				<b-aspect aspect="1">
				<table>
					<tr  v-for="(n,i) in 9">
						<td  v-for="(m,j) in 9" style="text-align:center">
							<span v-if="cellDisabled(i,j)">{{board[j + 9*i]}}</span>
							<b-btn @click="pickCellValue(i,j)" variant="secondary" class="boardbtn" v-if="!cellDisabled(i,j)">{{board[j + 9*i]}}</b-btn>
						</td>
					</tr>
				</table>
				</b-aspect>
			</div>
			
			<div>
				<b-btn variant="success" @click="validate()">Validate</b-btn>
				<b-btn variant="primary" @click="getNewGame()">New Game</b-btn>
				<router-link to="/newgame"><b-btn variant="danger">Back</b-btn></router-link>
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
