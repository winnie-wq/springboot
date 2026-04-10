export async function request(url, options = {}) {
  const headers = {
    'Content-Type': 'application/json',
    ...options.headers,
  }
  const res = await fetch(url, { ...options, headers })
  const text = await res.text()
  let data = {}
  try {
    data = text ? JSON.parse(text) : {}
  } catch {
    data = { code: res.ok ? 200 : res.status, message: text }
  }
  return data
}
