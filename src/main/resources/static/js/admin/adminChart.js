// jQuery를 사용해 문서 준비 상태 확인
$(document).ready(function () {
// Area Chart Example
    var ctx = document.getElementById("myAreaChart");
    var myLineChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: [], // 날짜 레이블이 들어갑니다.
            datasets: [
                {
                    label: "접속횟수",
                    lineTension: 0.3,
                    borderColor: "rgb(90,176,253)",
                    pointRadius: 5,
                    pointBackgroundColor: "rgb(50,161,255)",
                    pointBorderColor: "rgba(255,255,255,0.8)",
                    pointHoverRadius: 5,
                    pointHoverBackgroundColor: "rgb(106,165,218)",
                    pointHitRadius: 50,
                    pointBorderWidth: 2,
                    data: [],// 접속횟수 데이터
                    fill: false // 내부 채우기 비활성화
                },
                {
                    label: "접속자수",
                    lineTension: 0.3,
                    borderColor: "rgb(206,84,255)",
                    pointRadius: 5,
                    pointBackgroundColor: "rgb(209,77,244)",
                    pointBorderColor: "rgba(255,255,255,0.8)",
                    pointHoverRadius: 5,
                    pointHoverBackgroundColor: "rgb(197,127,223)",
                    pointHitRadius: 50,
                    pointBorderWidth: 2,
                    data: [],// 접속자수 데이터
                    fill: false // 내부 채우기 비활성화
                }
            ],
        },
        options: {
            responsive: true,
            scales: {
                x: {
                    type: 'time', // 시간 축으로 설정
                    time: {
                        unit: 'day'
                    },
                    grid: {
                        display: false
                    },
                    ticks: {
                        maxTicksLimit: 7
                    }
                },
                y: {
                    beginAtZero: true, // Y축 0부터 시작
                    ticks: {
                        maxTicksLimit: 5
                    },
                    grid: {
                        color: "rgba(0, 0, 0, .125)"
                    }
                },
                y2: {
                    beginAtZero: true, // 보조 Y축 (옵션)
                    position: "right",
                    ticks: {
                        maxTicksLimit: 5,
                        min: 0,
                        max: 400
                    },
                    grid: {
                        display: false
                    }
                }
            },
            plugins: {
                legend: {
                    display: true, // 범례 표시
                    position: 'top'
                }
            }
        }
    });


    /* 검색창 년도 데이터 넣기*/
    const currentYear = new Date().getFullYear();

    // 년도 추가 (2020년부터 현재 년도까지)
    for (let year = 2020; year <= currentYear; year++) {
        $('#yearSelect').append(`<option value="${year}">${year}</option>`);
    }

    // 월 추가 (1월부터 12월까지)
    const months = [
        '1월', '2월', '3월', '4월', '5월', '6월',
        '7월', '8월', '9월', '10월', '11월', '12월'
    ];
    months.forEach((month, index) => {
        $('#monthSelect').append(`<option value="${index + 1}">${month}</option>`);
    });


    // 오늘 날짜 객체 생성
    const today = new Date();

    // 년도와 월 구하기
    const year = today.getFullYear(); // 오늘의 년도 (예: 2024)
    const month = today.getMonth() + 1; // 오늘의 월 (0 ~ 11이므로 +1 필요)


    //오늘 날짜를 표시
    $('#yearSelect').val(year); // 현재 년도를 기본 선택
    $('#monthSelect').val(month); // 현재 월을 기본 선택


    function getData(year, month) {


        $.ajax({
            url: `/admin/dash?year=${year}&month=${month}`,
            type: 'GET',
            success: function (response) {
                console.log(response)
                setData(response.data);
            },
            error: function (xhr, status, error) {
                console.error("데이터 로드 중 오류 발생:", error);
            }
        });
    }

    function setData(data) {
        let data1 = [];
        data1.push(...data.visitors.map(item => item.count));

        let data2 = [];
        data2.push(...data.visitCount.map(item => item.count));

        myLineChart.data.labels = data.visitCount.map(item => item.date);

        myLineChart.data.datasets[0].data = data2;
        myLineChart.data.datasets[1].data = data1;

        myLineChart.update();
    }

    getData(year, month);

    // 수정 버튼 클릭 이벤트
    $(document).on("click", "#applyButton", function () {
        let month = $("#monthSelect").val();
        let year = $("#yearSelect").val();
        getData(year, month)

    });

});
