function createGraph(elem,type, labels, datasets){
    const graph_data = {
    labels: labels,
    datasets: datasets
    };

    const config = {
    type:type,
    data:graph_data
    }
    const result_chart = new Chart(elem, 
        config
    );
    return result_chart;
}