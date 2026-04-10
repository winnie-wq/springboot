import { request } from './http'

export default {
  getAll() {
    return request('/api/species')
  },
  create(body) {
    return request('/api/species', { method: 'POST', body: JSON.stringify(body) })
  },
  update(id, body) {
    return request(`/api/species/${id}`, { method: 'PUT', body: JSON.stringify(body) })
  },
  delete(id) {
    return request(`/api/species/${id}`, { method: 'DELETE' })
  },
}
