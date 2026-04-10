import { request } from './http'

export default {
  getTree() {
    return request('/api/categories/tree')
  },
  create(body) {
    return request('/api/categories', { method: 'POST', body: JSON.stringify(body) })
  },
  update(id, body) {
    return request(`/api/categories/${id}`, { method: 'PUT', body: JSON.stringify(body) })
  },
  delete(id) {
    return request(`/api/categories/${id}`, { method: 'DELETE' })
  },
}
