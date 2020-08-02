const Start = {
	template: `
	<div>
		<p>
		<router-link to="/newgame"><b-btn>Start a new game</b-btn></router-link>
		</p>
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
			if(this.diff >= (1-(17/81))){
				return 'Less than 17 clues'
			}
			return ''
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
			pickCellValueCellIndex:0
		}
	},
	mounted (){
		this.getNewGame();
	},
	methods: {
		getNewGame(){
			axios.get('/api/generateByDiff',{params:{diff:this.diff}}).then(res => {
				let str = res.data.substr(1);
			    this.setBoardByStr(str);
			});
		},
		validate(){		
			let str = this.getStrFromBoard();
			axios.get('/api/validate',{params:{str:str}}).then(res => {
				if(res.data == true){
					this.$bvToast.toast('Congrats! The board is valid.', {
						  toaster: 'b-toaster-top-left',
				          title: 'Validation Result',
				          variant: 'success'
				    })
				} else {
					this.$bvToast.toast('Oops! The board is invalid.', {
						  toaster: 'b-toaster-top-left',
				          title: 'Validation Result',
				          variant: 'danger'
				    })
				}
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
		<p>
		<h3>Difficulty of this game: {{diff}}</h3>
		<b-alert show variant="danger">The problem generated is not correct (there are repeats in each of the 9 subarea). Need revision.</b-alert>
		</p>
		<div class="board m-3">
			
			<table>
				<tr  v-for="(n,i) in 9">
					<td  v-for="(m,j) in 9" style="text-align:center">
						<span v-if="cellDisabled(i,j)">{{board[j + 9*i]}}</span>
						<b-btn @click="pickCellValue(i,j)" variant="secondary" class="boardbtn" v-if="!cellDisabled(i,j)">{{board[j + 9*i]}}</b-btn>
					</td>
				</tr>
			</table>
		</div>
		<div>
			<b-btn variant="success" @click="validate()">Validate</b-btn>
			<b-btn variant="primary" @click="getNewGame()">New Game</b-btn>
			<router-link to="/newgame"><b-btn variant="danger">Back</b-btn></router-link>
		</div>
		
		<b-modal id="pick-cell-modal" hide-footer hide-header>
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
