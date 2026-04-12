const TOKEN_KEY = 'mp_token'
const USER_ID_KEY = 'mp_userId'
const USERNAME_KEY = 'mp_username'
const NICKNAME_KEY = 'mp_nickname'

export function setAuth(loginData) {
  if (!loginData) return
  localStorage.setItem(TOKEN_KEY, loginData.token)
  localStorage.setItem(USER_ID_KEY, String(loginData.userId))
  localStorage.setItem(USERNAME_KEY, loginData.username || '')
  localStorage.setItem(NICKNAME_KEY, loginData.nickname || '')
}

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function getUserId() {
  const v = localStorage.getItem(USER_ID_KEY)
  return v ? Number(v) : 1
}

export function setUserId(id) {
  localStorage.setItem(USER_ID_KEY, String(id))
}

export function getUsername() {
  return localStorage.getItem(USERNAME_KEY) || ''
}

export function clearAuth() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_ID_KEY)
  localStorage.removeItem(USERNAME_KEY)
  localStorage.removeItem(NICKNAME_KEY)
}

export function isLoggedIn() {
  return !!getToken()
}
