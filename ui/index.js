import { h, app } from 'hyperapp'
import debounce from 'debounce-promise'
import Chart from 'chart.js'

import './styles/main.sass'
import config from './config.js'

const getDataFn = () => {
  return fetch(`http://${config.api.host}:${config.api.port}/yields`)
    .then(res => res.json())
}

const getData = debounce(getDataFn, 700)

const plot = data => {
  const dates = data.map(d => d.date)
  const values = data.map(d => d.yield)
  const low = 0.8*Math.min(values)
  new Chart('chart', {
    type: 'line',
    data: {
      labels: dates,
      datasets: [{
        label: 'Yield',
        data: values
      }]
    }
  });
}

const state = {
  data: null
}

const actions = {
  updateChart: () => (state, actions) => {
    getData().then(actions.setData)
  },
  setData: data => state => ({ data })
}

const view = (state, actions) =>
  <main>
    <div>yield(t) <a href='#' onclick={actions.updateChart}>refresh</a></div>
    <div>
      <canvas oncreate={actions.updateChart} id='chart'></canvas>
      {state.data ? (
        <div>
          {plot(state.data)}
        </div>
      ) : (
        <div>No data available</div>
      )}
    </div>
  </main>

app(state, actions, view, document.body)
