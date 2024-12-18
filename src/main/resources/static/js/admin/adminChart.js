// jQuery를 사용해 문서 준비 상태 확인
$(document).ready(function () {
    // 오늘 날짜 객체 생성
    const today = new Date();

    // 년도와 월 구하기
    const year = today.getFullYear(); // 오늘의 년도 (예: 2024)
    const month = today.getMonth() + 1; // 오늘의 월 (0 ~ 11이므로 +1 필요)


    // 데이터 및 설정
    var ctx = document.getElementById('myAreaChart').getContext('2d');
    var myAreaChart = new Chart(ctx, {
        type: 'line', // Area Chart는 라인 차트에서 background 설정 추가
        data: {
            labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'], // X축 라벨
            datasets: [{
                label: 'My Area Chart Example',
                data: [10, 25, 50, 75, 60, 90, 120], // 임의의 데이터
                backgroundColor: 'rgba(75, 192, 192, 0.2)', // 배경 색상 (Area 부분)
                borderColor: 'rgba(75, 192, 192, 1)', // 선 색상
                borderWidth: 2,
                fill: true, // 영역 채우기 설정
                tension: 0.4 // 곡선 부드럽게
            }]
        },
        options: {
            responsive: true,
            scales: {
                x: {
                    grid: {
                        display: false // X축 그리드 비활성화
                    }
                },
                y: {
                    beginAtZero: true, // Y축 시작점 0
                    grid: {
                        display: true
                    }
                }
            },
            plugins: {
                legend: {
                    display: true,
                    position: 'top'
                }
            }
        }
    });



});
