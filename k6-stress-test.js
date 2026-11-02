import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

export const options = {
  scenarios: {
    contention: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '10s', target: 50 },
      ],
      gracefulRampDown: '5s',
    },
    sustained_read: {
      executor: 'constant-vus',
      vus: 20,
      duration: '15s',
      startTime: '15s',
    },
    spike: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '10s', target: 30 },
      ],
      startTime: '35s',
    },
  },
  thresholds: {
    http_req_duration: ['p(95)<500'],
    http_req_failed: ['rate<0.01'],
  },
};

const BASE_URL = 'http://localhost:8080';
const authToken = __ENV.AUTH_TOKEN || '';
const headers = { 'Content-Type': 'application/json', 'Authorization': `Bearer ${authToken}` };

const errorRate = new Rate('errors');

export function setup() {
  const register = http.post(`${BASE_URL}/auth/register`, JSON.stringify({email: 'k6@test.com', password: 'password'}), {headers: {'Content-Type': 'application/json'}});
  const login = http.post(`${BASE_URL}/auth/login`, JSON.stringify({email: 'k6@test.com', password: 'password'}), {headers: {'Content-Type': 'application/json'}});
  return { token: login.json().token };
}

export default function (data) {
  const h = { ...headers, 'Authorization': `Bearer ${data.token}` };
  
  if (__ITER === 0 && __VU <= 50) {
    const res = http.post(`${BASE_URL}/bookings/lock-seat`, JSON.stringify({seatId: 1}), {headers: h});
    check(res, { 'lock status 201 or 409': (r) => r.status === 201 || r.status === 409 });
    errorRate.add(res.status !== 201 && res.status !== 409);
  }
  
  const events = http.get(`${BASE_URL}/events?page=0&size=20`, {headers: h});
  check(events, { 'events 200': (r) => r.status === 200 });
  errorRate.add(events.status !== 200);
  
  sleep(0.1);
}