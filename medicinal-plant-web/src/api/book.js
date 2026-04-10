import { request } from './http'

export default {
  getAll() {
    return request('/api/books')
  },
  search(keyword) {
    return request(`/api/books/search?keyword=${encodeURIComponent(keyword)}`)
  },
  getById(id) {
    return request(`/api/books/${id}`)
  },
  create(body) {
    return request('/api/books', { method: 'POST', body: JSON.stringify(body) })
  },
  update(id, body) {
    return request(`/api/books/${id}`, { method: 'PUT', body: JSON.stringify(body) })
  },
  delete(id) {
    return request(`/api/books/${id}`, { method: 'DELETE' })
  },
}
