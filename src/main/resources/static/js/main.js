var gameLifeApi = Vue.resource('http://localhost:8075/gameLife');

Vue.component('playingField', {
    props: {
        playingFieldSize: Number,
        selectedCells: Array
    },

    template: '<table cellspacing="0">\n' +
        '         <tr v-for="column in playingFieldSize">\n' +
        '             <td :id="[column,line]" v-for="line in playingFieldSize" class="square" @click="selectCell"/>\n' +
        '          </tr>\n' +
        '      </table>\n',

    methods: {
        selectCell: function (event) {
            if (event.target.style.background) {
                event.target.style.background = "";
            } else {
                event.target.style.background = "black";
            }
            this.selectedCells.push(event.target.id);
        }
    }
})

Vue.component('controlButtons', {
    props: ['startGame'],
    template: '<input type="button" value="Начать игру" @click="startGame"/>'
})

new Vue({
    el: '#app',

    template: '<div align="center">' +
        '           <playingField  v-bind:playingFieldSize="playingFieldSize" v-bind:selectedCells="selectedCells"/>' +
        '               <div v-if="playingFieldSize < 1">\n' +
        '                   <h2>Добрейший вечерочек</h2>\n' +
        '                   <h4>Введите размер игрового поля</h4>\n' +
        '                   от 10 до 30 клеток:<input v-model.lazy.number.trim="inputFieldSize"/>\n' +
        '               </div>' +
        '<controlButtons v-bind:startGame="startGame" v-if="playingFieldSize > 9"/>' +
        '  <br/>{{selectedCells}}     </div>',

    data: {
        inputFieldSize: null,   //размер игрового поля, введенный пользователем
        playingFieldSize: null, //размер игрового поля
        selectedCells: [],      //выбранные пользователем ячейки
        gameIsStarted: false    //проверка на запуск игры 
    },
    methods: {

        startGame: function () {

            this.gameIsStarted = true;//игра началась
            var cells = [];

            this.selectedCells.forEach(result => {
                var cell = {
                    y: result.split(',')[0],
                    x: result.split(',')[1],
                    life:true
                }
                cells.push(cell);
        })
            var game =
                {
                    playingFieldSize: this.playingFieldSize,
                    cells: cells
                };
            this.selectedCells = [];
            gameLifeApi.save(game).then(response => {
                response.json().then(data => {
                    console.log(data);                       //сюда приходит игра с новым поколением живых клеток
                    this.playingFieldSize = data.playingFieldSize;
                    data.cells.forEach(cell=>{
                        newCell = cell.y + ',' + cell.x;
                        document.getElementById(newCell).bgColor = "black";
                        this.selectedCells.push(newCell); 
                    })
                })
            ;
        },
            response =>{
                alert("При ожидании данных с сервера что-то пошло не так, как должно...")
            }
        )

        //процесс игры в цикле
        if(this.selectedCells && this.gameIsStarted){
            setTimeout(this.startGame, 1000);
        }

        }

    },

    // created: function () {
    //     gameLifeApi.get().then(result => {
    //         console.log(result)
    //     })
    // },

    watch: {
        inputFieldSize: function (val) {
            val = parseInt(val, 10);
            val > 9 && val < 31 ? this.playingFieldSize = val : alert("Введите значение от 10 до 30");
        }
    }
})