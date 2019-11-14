var gameLifeApi = Vue.resource('http://localhost:8075/gameLife');

Vue.component('playingField', {
    props: ['playingFieldSize', 'selectedCells', 'gameIsStartedTable'],

    template: '<table cellspacing="0">\n' +
        '         <tr v-for="column in playingFieldSize">\n' +
        '             <td :id="[column,line]" v-for="line in playingFieldSize" class="square"  v-on:mouseover="selectCell"/>\n' +
        '          </tr>\n' +
        '      </table>\n',

    methods: {
        selectCell: function (event) {
            if (this.gameIsStartedTable == true) {
                return;
            }
            if (document.getElementById(event.target.id).bgColor) {
                document.getElementById(event.target.id).bgColor = "";
                this.selectedCells.splice(this.selectedCells.indexOf(event.target.id), 1)
            } else {
                document.getElementById(event.target.id).bgColor = "black";
                this.selectedCells.push(event.target.id);
            }

        }
    },
})

Vue.component('controlButtons', {
    props: ['startGame', 'stopGame'],
    template: '<div>' +
        '<input type="button" value="Начать игру" @click="startGame"/>' +
        '<input type="button" value="Остановить игру" @click="stopGame"/>' +
        '</div>'
})

new Vue({
    el: '#app',

    template: '<div align="center">' +
        '           <playingField  v-bind:playingFieldSize="playingFieldSize" v-bind:gameIsStartedTable="gameIsStarted" v-bind:selectedCells="selectedCells"/>' +
        '               <div v-if="playingFieldSize < 1">\n' +
        '                   <h2>Добрейший вечерочек</h2>\n' +
        '                   <h4>Введите размер игрового поля</h4>\n' +
        '                   от 20 до 50 клеток:<input v-model.lazy.number.trim="inputFieldSize"/>\n' +
        '               </div>' +
        '<controlButtons v-bind:startGame="startGame" v-bind:stopGame="stopGame" v-if="playingFieldSize > 9"/>' +
        '  <br/></div>',

    data: {
        inputFieldSize: null,   //размер игрового поля, введенный пользователем
        playingFieldSize: null, //размер игрового поля
        selectedCells: [],      //выбранные пользователем ячейки
        oldGenerationCells: [], //старое поколение ячеек
        newGenerationCells: [], //новое поколение ячеек
        gameIsStarted: false    //проверка на запуск игры 
    },
    methods: {

        startGame: function () {
 
            this.gameIsStarted = true;

            let cells = [];
            for (let i = 0; i < this.selectedCells.length; i++) {
                let cell = {
                    y: this.selectedCells[i].split(',')[0],
                    x: this.selectedCells[i].split(',')[1],
                    life: true
                };
                cells.push(cell)
            }

            let game =
                {
                    playingFieldSize: this.playingFieldSize,
                    cells: cells
                };

            for (let i = 0; i < this.oldGenerationCells.length; i++){
                document.getElementById(this.oldGenerationCells[i]).bgColor = ""; //чистка поля от мертвых клеток
            }
            this.newGenerationCells = [];

            gameLifeApi.save(game).then(response => {
                response.json().then(data => {
                    this.playingFieldSize = data.playingFieldSize;

                    let newCell;
                    for (let i = 0; i < data.cells.length; i++) {
                        newCell = data.cells[i].y + ',' + data.cells[i].x;
                        this.newGenerationCells.push(newCell);
                    }

                    if (this.newGenerationCells.length !== 0 && this.gameIsStarted) {
                        for (let i = 0; i < this.newGenerationCells.length; i++){
                            if (document.getElementById(this.newGenerationCells[i]).bgColor !== "black"){
                                document.getElementById(this.newGenerationCells[i]).bgColor = "black";
                                let targetCell = this.selectedCells.indexOf(this.newGenerationCells[i]);
                                if (targetCell > -1) this.selectedCells.splice(targetCell, 1);
                            }
                        }

                        this.oldGenerationCells = this.selectedCells;
                        this.selectedCells = this.newGenerationCells;
                        setTimeout(this.startGame, 10);
                    }
                });
            },
                response => {
                    alert("При ожидании данных с сервера что-то пошло не так, как должно...")
                }
            )
        },

        stopGame: function () {
            this.gameIsStarted = false;
            this.selectedCells.forEach(result => {
                document.getElementById(result).bgColor = "";
            });
            this.selectedCells = [];
        }
    },

 

    watch: {
        inputFieldSize: function (val) {
            val = parseInt(val, 10);
            val > 19 && val < 51 ? this.playingFieldSize = val : alert("Введите значение от 20 до 50");
        }
    },

    created: function () {
        gameLifeApi.get().then(response => {
            if(response.body !== ""){
                this.playingFieldSize = response.body.playingFieldSize;
                response.body.cells.forEach(cell => {
                    this.selectedCells.push(cell.y + ',' + cell.x)
                });
                this.startGame()
            }
        })   
    }
});