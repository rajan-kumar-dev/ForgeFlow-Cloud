import { useState, useEffect, type FormEvent } from 'react'
import {
  Activity,
  ArrowRight,
  ArrowUpRight,
  CircleHelp,
  Clock3,
  Cpu,
  Layers3,
  LockKeyhole,
  Mail,
  Plus,
  Search,
  Server,
  CircleCheck,
  AlertTriangle,
  Download,
  ListTodo,
  Gauge,
  X,
  LoaderCircle,
  RefreshCw,
  Rss,
  ShieldCheck,
  Workflow,
} from 'lucide-react'
import { SignIn, UserButton, useAuth, useUser } from '@clerk/react'

type Feature = {
  title: string
  description: string
  icon: typeof Workflow
}

const features: Feature[] = [
  {
    title: 'Durable execution',
    description: 'Persist every job, track every attempt, and recover work when a worker goes down.',
    icon: Layers3,
  },
  {
    title: 'Automatic retries',
    description: 'Handle transient failures with backoff, retry limits, and a place for exhausted jobs.',
    icon: RefreshCw,
  },
  {
    title: 'Workers that scale',
    description: 'Distribute background tasks across workers and scale capacity as the queue grows.',
    icon: Cpu,
  },
  {
    title: 'Live observability',
    description: 'See job state, attempt history, queue pressure, and service health in one control room.',
    icon: Activity,
  },
]

function BrandMark({ compact = false }: { compact?: boolean }) {
  return (
    <a className={`brand ${compact ? 'brand-compact' : ''}`} href="#home" aria-label="ForgeFlow home">
      <svg className="brand-symbol" viewBox="0 0 44 42" aria-hidden="true">
        <path d="M6 5.5C7.5 2.5 10 1 14 1h28L36.8 9.4H2.2L6 5.5Z" fill="#61F0B8" />
        <path d="M2.4 17.2c1.7-3.1 4.4-4.7 8.3-4.7h27.1l-5.2 8.3H0.3l2.1-3.6Z" fill="#39D7A0" />
        <path d="M3.5 28.2c1.5-2.8 4-4.2 7.6-4.2h17.3l-5.4 8.4H0l3.5-4.2Z" fill="#239D77" />
      </svg>
      <span>ForgeFlow</span>
    </a>
  )
}

function GoogleMark() {
  return (
    <svg viewBox="0 0 48 48" className="google-mark" aria-hidden="true">
      <path fill="#EA4335" d="M24 9.5c3.54 0 6.71 1.22 9.21 3.6l6.85-6.85C35.9 2.38 30.47 0 24 0 14.62 0 6.51 5.38 2.56 13.22l7.98 6.19C12.43 13.72 17.74 9.5 24 9.5Z" transform="translate(0 4) scale(.92)" />
      <path fill="#4285F4" d="M46.98 24.55c0-1.57-.15-3.09-.38-4.55H24v9.02h12.94c-.58 2.96-2.26 5.48-4.76 7.18l7.73 6C44.42 38.04 46.98 31.98 46.98 24.55Z" transform="translate(0 1) scale(.92)" />
      <path fill="#FBBC05" d="M10.53 28.59A14.4 14.4 0 0 1 9.75 24c0-1.59.27-3.13.76-4.59l-7.98-6.19A23.9 23.9 0 0 0 0 24c0 3.88.93 7.56 2.56 10.78l7.97-6.19Z" transform="translate(2 1) scale(.92)" />
      <path fill="#34A853" d="M24 48c6.48 0 11.93-2.13 15.91-5.8l-7.73-6c-2.14 1.44-4.89 2.3-8.18 2.3-6.26 0-11.57-4.22-13.47-9.91l-7.98 6.19C6.51 42.62 14.62 48 24 48Z" transform="translate(0 -2) scale(.92)" />
    </svg>
  )
}

function FeatureCard({ feature, index }: { feature: Feature; index: number }) {
  const Icon = feature.icon
  return (
    <article className="feature-card" style={{ animationDelay: `${index * 90}ms` }}>
      <div className="feature-icon"><Icon size={19} strokeWidth={1.8} /></div>
      <h3>{feature.title}</h3>
      <p>{feature.description}</p>
      <span className="card-index">0{index + 1}</span>
    </article>
  )
}

function BrandAside() {
  return (
    <section className="intro-panel" id="home">
      <div className="intro-topline"><BrandMark /></div>
      <div className="intro-content">
        <div className="eyebrow"><span className="eyebrow-dot" /> BACKGROUND JOB INFRASTRUCTURE</div>
        <h1>Ship the work.<br /><span>We’ll handle the retries.</span></h1>
        <p className="hero-copy">
          A reliable job processing platform for engineering teams. Queue background work,
          recover from failures, and understand what happened without building the machinery yourself.
        </p>
        <div className="feature-grid">
          {features.map((feature, index) => <FeatureCard key={feature.title} feature={feature} index={index} />)}
        </div>
      </div>
      <footer className="intro-footer">
        <span className="built-label">BUILT FOR ENGINEERS</span>
        <div className="tech-list" aria-label="Technology stack">
          <span>Java</span><i />
          <span>Spring Boot</span><i />
          <span>RabbitMQ</span><i />
          <span>PostgreSQL</span><i />
          <span>Docker</span><i />
          <span>Kubernetes</span>
        </div>
        <a className="learn-link" href="#architecture">
          <CircleHelp size={15} /> What is ForgeFlow?
        </a>

      </footer>
      <div className="architecture-note" id="architecture">
        <strong>ForgeFlow, in one sentence</strong>
        <span>Submit a job once. Track execution, retry eligible failures, and inspect the outcome.</span>
      </div>
    </section>
  )
}

function ClerkAppearance() {
  return {
    variables: {
      colorPrimary: '#58e9b3',
      colorBackground: '#0c0d10',
      colorText: '#f6f7f8',
      colorTextSecondary: '#9b9da7',
      colorInputBackground: '#1c1d22',
      colorInputText: '#f6f7f8',
      colorNeutral: '#a3a5ad',
      borderRadius: '10px',
      fontFamily: 'DM Sans, sans-serif',
    },
    elements: {
      rootBox: { width: '100%' },
      card: { width: '100%', boxShadow: 'none', backgroundColor: 'transparent', border: 'none', padding: '0' },
      headerTitle: { fontFamily: 'Space Grotesk, sans-serif', fontSize: '22px', letterSpacing: '-0.04em' },
      headerSubtitle: { color: '#92949f', fontSize: '13px' },
      socialButtonsBlockButton: { backgroundColor: '#111216', border: '1px solid #2c2e34', color: '#f4f5f7', height: '46px' },
      socialButtonsBlockButtonText: { fontWeight: '600', fontSize: '13px' },
      dividerLine: { background: '#2c2e34' },
      dividerText: { color: '#858792', fontSize: '11px' },
      formFieldLabel: { color: '#d7d8dd', fontSize: '12px', fontWeight: '600' },
      formFieldInput: { backgroundColor: '#202126', border: '1px solid #303139', color: '#ffffff', height: '44px' },
      formButtonPrimary: { backgroundColor: '#5beab5', color: '#07120e', fontWeight: '700', height: '46px' },
      footer: { backgroundColor: 'transparent' },
      footerActionLink: { color: '#62e9b7' },
      identityPreviewText: { color: '#f6f7f8' },
      formFieldAction: { color: '#60eab6' },
    },
    layout: {
      socialButtonsPlacement: 'top' as const,
      socialButtonsVariant: 'blockButton' as const,
      showOptionalFields: false,
    },
  }
}

function AuthPanelPreview({ onDemo }: { onDemo: () => void }) {
  const [mode, setMode] = useState<'sign-in' | 'sign-up'>('sign-in')
  const [notice, setNotice] = useState('')
  const [email, setEmail] = useState('')
  const activateNotice = () => setNotice('Configure VITE_CLERK_PUBLISHABLE_KEY and enable Google in Clerk to activate real authentication. No login was attempted.')
  return <div className="auth-card preview-auth-card">
    <div className="auth-brand"><span className="auth-brand-mark"><BrandMark compact /></span></div>
    <div className="auth-heading"><h2>{mode === 'sign-in' ? 'Sign in to ForgeFlow' : 'Create your ForgeFlow account'}</h2><p>{mode === 'sign-in' ? 'Welcome back. Your jobs are waiting.' : 'Start building reliable background jobs.'}</p></div>
    <button type="button" className="google-button" onClick={activateNotice}><GoogleMark /><span>Continue with Google</span><ArrowUpRight size={14} className="button-arrow" /></button>
    <div className="auth-divider"><span /><em>or continue with email</em><span /></div>
    <form className="auth-form" onSubmit={(event) => { event.preventDefault(); activateNotice() }}>
      <label htmlFor="email">Email address</label><div className="input-wrap"><Mail size={16} /><input id="email" type="email" placeholder="you@company.com" value={email} onChange={(event) => setEmail(event.target.value)} required /></div>
      <button className="continue-button" type="submit">Continue <ArrowRight size={15} /></button>
    </form>
    {notice && <div className="setup-notice" role="status"><LockKeyhole size={15} /><span>{notice}</span></div>}
    <div className="auth-switch">{mode === 'sign-in' ? 'New to ForgeFlow?' : 'Already have an account?'}{' '}<button type="button" onClick={() => { setMode(mode === 'sign-in' ? 'sign-up' : 'sign-in'); setNotice('') }}>{mode === 'sign-in' ? 'Create account' : 'Sign in'}</button></div>
    <div className="auth-meta"><ShieldCheck size={13} /> Authentication powered by Clerk</div>
    <button type="button" className="preview-demo-button" onClick={onDemo}>Open local demo workspace <ArrowRight size={13} /></button>
    <div className="dev-mode"><span /> Local preview · no authentication enabled</div>
  </div>
}

function AuthPanelLive() {
  return <div className="auth-card live-auth-card"><div className="auth-brand"><span className="auth-brand-mark"><BrandMark compact /></span></div><SignIn routing="hash" fallbackRedirectUrl="/" signUpFallbackRedirectUrl="/" appearance={ClerkAppearance()} /><div className="auth-meta"><ShieldCheck size={13} /> Authentication powered by Clerk</div></div>
}

function LoginPanel({ live = false, onDemo }: { live?: boolean; onDemo?: () => void }) {
  return <aside className="login-panel"><div className="login-glow" /><div className="login-overline"><span className="status-dot" /> YOUR BACKGROUND WORK, UNDER CONTROL</div><div className="auth-position">{live ? <AuthPanelLive /> : <AuthPanelPreview onDemo={onDemo ?? (() => {})} />}</div><div className="login-footnote"><LockKeyhole size={12} /> Secure sign-in · Built for engineering teams</div></aside>
}

function LandingPage({ live = false, onDemo }: { live?: boolean; onDemo?: () => void }) {
  return <main className="page-shell"><BrandAside /><LoginPanel live={live} onDemo={onDemo} /></main>
}

type Job = {
  id: string; type: string; status: string; priority: string; attempts: number; maxAttempts: number;
  createdAt: string; updatedAt: string; lastError?: string | null; result?: { fileName?: string; downloadPath?: string; sampleData?: boolean; [key: string]: unknown } | null;
  payload?: Record<string, unknown>;
}
type Worker = { workerId: string; activeJobs: number; lastSeen: string; healthy: boolean }

function Dashboard({ getToken, onSignOut, localPreview = false }: { getToken?: () => Promise<string | null>; onSignOut?: () => void; localPreview?: boolean }) {
  const [jobs, setJobs] = useState<Job[]>([])
  const [summary, setSummary] = useState<Record<string, number>>({})
  const [workers, setWorkers] = useState<Worker[]>([])
  const [tab, setTab] = useState<'overview' | 'jobs' | 'create' | 'workers' | 'failure'>('overview')
  const [jobType, setJobType] = useState<
    'GENERATE_REPORT' | 'SIMULATE_FAILURE' | 'FETCH_RSS_FEED'
  >('GENERATE_REPORT')
  const [feedUrl, setFeedUrl] = useState('https://www.nasa.gov/feed/')
  const [priority, setPriority] = useState('NORMAL')
  const [lookbackDays, setLookbackDays] = useState(30)
  const [failFirstAttempts, setFailFirstAttempts] = useState(2)
  const [maxAttempts, setMaxAttempts] = useState(3)
  const [scheduleMode, setScheduleMode] = useState('NOW')
  const [runAt, setRunAt] = useState('')
  const [busy, setBusy] = useState(false)
  const [apiError, setApiError] = useState('')
  const [apiStatus, setApiStatus] = useState<'checking' | 'connected' | 'issue'>('checking')
  const [notice, setNotice] = useState('')
  const [query, setQuery] = useState('')
  const [statusFilter, setStatusFilter] = useState('ALL')
  const [adminError, setAdminError] = useState('')
  const apiBase = (import.meta.env.VITE_API_BASE_URL || '/api').replace(/\/$/, '')
  const apiUrl = (path: string) => `${apiBase}${path.startsWith('/') ? path : `/${path}`}`
  const request = async <T,>(path: string, init: RequestInit = {}): Promise<T> => {
    const token = getToken ? await getToken() : null
    const headers = new Headers(init.headers)
    if (init.body && !headers.has('Content-Type')) headers.set('Content-Type', 'application/json')
    if (token) headers.set('Authorization', `Bearer ${token}`)
    const response = await fetch(apiUrl(path), { ...init, headers })
    if (!response.ok) {
      let detail = `${response.status} ${response.statusText}`
      try { const body = await response.json() as { error?: string; message?: string }; detail = body.error || body.message || detail } catch { /* keep HTTP error */ }
      throw new Error(detail)
    }
    if (response.status === 204) return undefined as T
    return await response.json() as T
  }
  const refresh = async () => {
    try {
      const [jobData, stats] = await Promise.all([request<Job[]>('/jobs'), request<Record<string, number>>('/dashboard/summary')])
      setJobs(jobData)
      setSummary(stats)
      setApiError('')
      setApiStatus('connected')
     } catch (error) {
        setApiStatus('issue')
        setApiError(
          error instanceof Error
            ? error.message
            : 'Could not connect to ForgeFlow API'
        )
      }
  }
  useEffect(() => { void refresh(); const timer = window.setInterval(() => { void refresh() }, 5000); return () => window.clearInterval(timer) }, [])
  const loadWorkers = async () => {
    try { const data = await request<Worker[]>('/admin/workers'); setWorkers(data); setAdminError('') }
    catch (error) { setWorkers([]); setAdminError(error instanceof Error ? error.message : 'Could not load worker data') }
  }
  const createJob = async (event: FormEvent) => {
    event.preventDefault(); setBusy(true); setNotice(''); setApiError('')
    try {
      const payload =
        jobType === 'GENERATE_REPORT'
          ? {
              reportType: 'SALES_SUMMARY',
              lookbackDays,
              format: 'CSV',
              dataLabel: 'SAMPLE DATA ONLY',
            }
          : jobType === 'SIMULATE_FAILURE'
            ? { failFirstAttempts }
            : { feedUrl }
      const requestKey = crypto.randomUUID()
      const created = await request<Job>('/jobs', { method: 'POST', headers: { 'Idempotency-Key': requestKey }, body: JSON.stringify({ type: jobType, payload, priority, scheduleMode, runAt: scheduleMode === 'LATER' && runAt ? new Date(runAt).toISOString() : null, maxAttempts, idempotencyKey: requestKey }) })
      setNotice(`Job ${created.id} was accepted with status ${created.status}.`); setTab('jobs'); await refresh()
    } catch (error) { setApiError(error instanceof Error ? error.message : 'Job submission failed') }
    finally { setBusy(false) }
  }
  const cancelJob = async (id: string) => { try { await request<Job>(`/jobs/${id}/cancel`, { method: 'POST' }); setNotice(`Cancellation requested for ${id}.`); await refresh() } catch (error) { setApiError(error instanceof Error ? error.message : 'Cancel failed') } }
  const retryJob = async (id: string) => { try { await request<Job>(`/admin/jobs/${id}/retry`, { method: 'POST' }); setNotice(`Manual retry queued for ${id}.`); await refresh() } catch (error) { setApiError(error instanceof Error ? error.message : 'Retry failed; admin access may be required.') } }
  const downloadResult = async (job: Job) => { try { const token = getToken ? await getToken() : null; const headers = new Headers(); if(token)headers.set('Authorization',`Bearer ${token}`); const response=await fetch(apiUrl(`/jobs/${job.id}/result`),{headers}); if(!response.ok)throw new Error(`${response.status} ${response.statusText}`); const blob=await response.blob(); const url=URL.createObjectURL(blob); const a=document.createElement('a');a.href=url;a.download=job.result?.fileName||`${job.id}.csv`;a.click();URL.revokeObjectURL(url) } catch(error) {setApiError(error instanceof Error?error.message:'Download failed')} }
  const setTabSafe = (next: typeof tab) => { setTab(next); setNotice(''); if(next==='workers')void loadWorkers() }
  const filteredJobs = jobs.filter(j => (statusFilter==='ALL'||j.status===statusFilter) && `${j.id} ${j.type} ${j.status}`.toLowerCase().includes(query.toLowerCase()))
  const stat = (label:string, key:string, icon:typeof Layers3) => {const Icon=icon;return <article className="metric-card"><div className="metric-top"><span>{label}</span><Icon size={17}/></div><strong>{summary[key] ?? 0}</strong><small>Live job records</small></article>}
  const nav = [{id:'overview' as const,label:'Overview',icon:Gauge},{id:'jobs' as const,label:'Jobs',icon:ListTodo},{id:'create' as const,label:'Create job',icon:Plus},{id:'workers' as const,label:'Workers & health',icon:Server},{id:'failure' as const,label:'Failure Lab',icon:RefreshCw}]
  return <main className="dashboard-shell">
    <aside className="dashboard-sidebar"><div className="sidebar-brand"><BrandMark /></div><div className="sidebar-label">WORKSPACE</div><nav className="dashboard-nav">{nav.map(item=>{const Icon=item.icon;return <button key={item.id} className={tab===item.id?'active':''} onClick={()=>setTabSafe(item.id)}><Icon size={17}/>{item.label}{item.id==='create'&&<span className="nav-plus">+</span>}</button>})}</nav><div className="sidebar-bottom"><div className="sidebar-status"><span className="live-dot"/> API connection <b>{apiStatus === 'connected'? 'Connected' : apiStatus === 'issue'? 'Issue': 'Checking'}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      </b></div><div className="sidebar-product">FORGEFLOW CLOUD <span>v1.0 starter</span></div></div></aside>
    <section className="dashboard-main"><header className="dashboard-header"><div><div className="eyebrow"><span className="eyebrow-dot"/> JOB ORCHESTRATION</div><h1>{tab==='overview'?'Control center':tab==='jobs'?'Job explorer':tab==='create'?'Create a job':tab==='workers'?'Workers & health':'Failure Lab'}</h1><p>{tab==='overview'?'Monitor background execution, retries and system activity.':tab==='jobs'?'Inspect job state, attempts and generated artifacts.':tab==='create'?'Submit a task and let ForgeFlow manage its lifecycle.':tab==='workers'?'Inspect worker heartbeats and privileged infrastructure health.':'Exercise automatic retries with a controlled test failure.'}</p></div><div className="header-actions">{localPreview&&<span className="dev-pill">LOCAL DEMO</span>}<button className="icon-action" onClick={()=>void refresh()} aria-label="Refresh"><RefreshCw size={16}/></button>{onSignOut&&<UserButton/>}</div></header>
      {apiError&&<div className="notice-banner error"><AlertTriangle size={17}/><div><strong>Backend connection or request issue</strong><span>{apiError}. Start the backend with <code>docker compose up --build</code> and check the service logs.</span></div><button onClick={()=>setApiError('')} aria-label="Dismiss"><X size={15}/></button></div>}
      {notice&&<div className="notice-banner success"><CircleCheck size={17}/><span>{notice}</span><button onClick={()=>setNotice('')} aria-label="Dismiss"><X size={15}/></button></div>}
      {tab==='overview'&&<><div className="metrics-grid">{stat('Total jobs','total',Layers3)}{stat('Running','running',Activity)}{stat('Succeeded','succeeded',CircleCheck)}{stat('Dead / failed','dead',AlertTriangle)}</div><div className="content-grid"><section className="panel jobs-panel"><div className="panel-heading"><div><h2>Recent jobs</h2><p>Latest background work submitted to ForgeFlow</p></div><button className="text-button" onClick={()=>setTabSafe('jobs')}>View all <ArrowRight size={14}/></button></div><JobTable jobs={jobs.slice(0,6)} onDownload={downloadResult} onCancel={cancelJob} onRetry={retryJob}/></section><section className="panel quick-panel"><div className="panel-heading"><div><h2>Quick actions</h2><p>Launch a supported task type</p></div></div><button className="quick-action" onClick={()=>{setJobType('GENERATE_REPORT');setTabSafe('create')}}><span className="quick-icon"><Layers3 size={17}/></span><span><b>Generate report</b><small>Creates an illustrative sales CSV</small></span><ArrowRight size={15}/></button><button className="quick-action" onClick={()=>{setJobType('SIMULATE_FAILURE');setTabSafe('create')}}><span className="quick-icon"><RefreshCw size={17}/></span><span><b>Test automatic retries</b><small>Simulate transient failures</small></span><ArrowRight size={15}/></button><button className="quick-action" onClick={()=>{setJobType('FETCH_RSS_FEED');setFeedUrl('https://www.nasa.gov/feed/');setTabSafe('create')}}><span className="quick-icon"><Rss size={17}/></span><span><b>Export an RSS feed</b><small>Fetch NASA or BBC feed articles as CSV</small></span><ArrowRight size={15}/></button><div className="info-callout"><ShieldCheck size={16}/><p>Reports are generated from labelled sample data in this starter. Connect a real business data source before using it for real reporting.</p></div></section></div></>}
      {tab==='jobs'&&<section className="panel full-panel"><div className="panel-heading"><div><h2>All jobs</h2><p>Refreshes automatically every five seconds</p></div><button className="primary-small" onClick={()=>setTabSafe('create')}><Plus size={14}/> Create job</button></div><div className="table-toolbar"><div className="search-input"><Search size={15}/><input value={query} onChange={e=>setQuery(e.target.value)} placeholder="Search by ID, type or status"/></div><select value={statusFilter} onChange={e=>setStatusFilter(e.target.value)}><option value="ALL">All statuses</option>{['QUEUED','RUNNING','RETRY_WAIT','SCHEDULED','SUCCEEDED','DEAD','CANCELLED'].map(s=><option key={s} value={s}>{s}</option>)}</select></div><JobTable jobs={filteredJobs} onDownload={downloadResult} onCancel={cancelJob} onRetry={retryJob}/></section>}
      {tab==='create'&&<div className="form-layout"><section className="panel create-panel"><div className="panel-heading"><div><h2>Configure your job</h2><p>Select a supported handler and execution policy.</p></div></div><form onSubmit={createJob} className="create-job-form">
          <label>
            Job type
            <select value={jobType} onChange={e => setJobType(e.target.value as typeof jobType)}>
              <option value="GENERATE_REPORT">Generate sales report</option>
              <option value="SIMULATE_FAILURE">Failure Lab — simulate transient errors</option>
              <option value="FETCH_RSS_FEED">Fetch RSS feed → CSV</option>
            </select>
          </label>

          {jobType === 'GENERATE_REPORT' ? (
            <label>
              Report period
              <select value={lookbackDays} onChange={e => setLookbackDays(Number(e.target.value))}>
                <option value={7}>Last 7 days</option>
                <option value={30}>Last 30 days</option>
                <option value={90}>Last 90 days</option>
              </select>
            </label>
          ) : jobType === 'FETCH_RSS_FEED' ? (
            <label>
              RSS feed URL
              <input
                type="url"
                value={feedUrl}
                onChange={e => setFeedUrl(e.target.value)}
                placeholder="https://www.nasa.gov/feed/"
                pattern="https://.*"
                required
              />
              <small className="field-help">
                Use HTTPS. Allowed hosts: www.nasa.gov and feeds.bbci.co.uk.
              </small>
            </label>
          ) : (
            <label>
              Number of attempts to fail
              <input
                type="number"
                min={0}
                max={8}
                value={failFirstAttempts}
                onChange={e => setFailFirstAttempts(Number(e.target.value))}
              />
              <small className="field-help">
                Set lower than max attempts to observe automatic recovery.
              </small>
            </label>
          )}<div className="two-fields"><label>Priority<select value={priority} onChange={e=>setPriority(e.target.value)}><option>LOW</option><option>NORMAL</option><option>HIGH</option></select></label><label>Maximum attempts<input type="number" min={1} max={10} value={maxAttempts} onChange={e=>setMaxAttempts(Number(e.target.value))}/></label></div><label>Schedule<select value={scheduleMode} onChange={e=>setScheduleMode(e.target.value)}><option value="NOW">Run as soon as possible</option><option value="LATER">Schedule for later</option></select></label>{scheduleMode==='LATER'&&<label>Run at<input type="datetime-local" value={runAt} onChange={e=>setRunAt(e.target.value)} required/></label>}<div className="form-note"><Clock3 size={16}/><span>ForgeFlow stores the job, publishes it to RabbitMQ, and executes it in a Java worker. Eligible failures are retried automatically.</span></div><button className="continue-button submit-job" type="submit" disabled={busy}>{busy?<><LoaderCircle size={16} className="spin"/> Submitting...</>:<>Submit job <ArrowRight size={15}/></>}</button></form></section><aside className="panel job-preview"><div className="panel-heading"><div><h2>Execution preview</h2><p>What ForgeFlow will do</p></div></div><div className="preview-step"><span>01</span><div><b>Persist job</b><small>Job Service writes the job record to PostgreSQL.</small></div></div><div className="preview-step"><span>02</span><div><b>Queue work</b><small>Outbox publisher sends the job ID through RabbitMQ.</small></div></div><div className="preview-step"><span>03</span><div><b>Execute and retry</b><small>Worker runs the handler and reports success or failure.</small></div></div><div className="preview-step"><span>04</span><div><b>Record outcome</b><small>Dashboard shows status, attempt history and report link.</small></div></div></aside></div>}
      {tab==='workers'&&<section className="panel full-panel"><div className="panel-heading"><div><h2>Worker registry</h2><p>Worker heartbeats are reported to the Job Service every five seconds.</p></div><button className="icon-action" onClick={()=>void loadWorkers()}><RefreshCw size={15}/></button></div>{adminError&&<div className="notice-banner error"><AlertTriangle size={17}/><div><strong>Worker details unavailable</strong><span>{adminError}. In secured mode, this endpoint requires an administrator role claim.</span></div></div>}{workers.length===0?<EmptyState title="No worker heartbeat received yet" body="Start the Worker Service with Docker Compose, then refresh this view."/>:<div className="worker-grid">{workers.map(w=><article className="worker-card" key={w.workerId}><div className="worker-card-top"><span className="quick-icon"><Cpu size={18}/></span><span className={w.healthy?'health-good':'health-bad'}>{w.healthy?'Healthy':'Stale'}</span></div><b>{w.workerId}</b><p>{w.activeJobs} active jobs</p><small>Last heartbeat: {new Date(w.lastSeen).toLocaleString()}</small></article>)}</div>}</section>}
      {tab==='failure'&&<section className="panel full-panel failure-panel"><div className="panel-heading"><div><h2>Failure Lab</h2><p>Run controlled test jobs to prove that the retry system works.</p></div><span className="dev-pill">TEST ONLY</span></div><div className="failure-explainer"><div className="quick-icon large"><RefreshCw size={21}/></div><div><h3>Make a job fail, then recover</h3><p>Create a simulated task configured to fail its first two attempts. ForgeFlow should record each failed attempt, schedule retries, and finish successfully once the configured failure count is passed.</p></div></div><button className="primary-small" onClick={()=>{setJobType('SIMULATE_FAILURE');setFailFirstAttempts(2);setMaxAttempts(3);setTabSafe('create')}}><Plus size={15}/> Configure Failure Lab job</button><p className="field-help">This handler intentionally throws an error for testing; it does not call external services.</p></section>}
      <footer className="dashboard-footer"><span>FORGEFLOW CLOUD · STARTER</span><span>Java · Spring Boot · RabbitMQ · PostgreSQL · Docker · Kubernetes</span></footer>
    </section>
  </main>
}

function JobTable({ jobs, onDownload, onCancel, onRetry }: { jobs: Job[]; onDownload: (job: Job) => void; onCancel: (id: string) => void; onRetry: (id: string) => void }) {
  if(!jobs.length) return <EmptyState title="No jobs yet" body="Create a report, RSS feed export, or Failure Lab task to see execution records here." />
  return <div className="table-wrap"><table className="jobs-table"><thead><tr><th>Job</th><th>Status</th><th>Attempts</th><th>Created</th><th>Actions</th></tr></thead><tbody>{jobs.map(job=><tr key={job.id}><td><div className="job-primary"><span className="job-mini-icon">{job.type === 'GENERATE_REPORT' ? <Layers3 size={15}/> : job.type === 'FETCH_RSS_FEED' ? <Rss size={15}/> : <RefreshCw size={15}/> }</span><div><b>{job.type === 'GENERATE_REPORT' ? 'Sales report' : job.type === 'FETCH_RSS_FEED' ? 'RSS feed export' : 'Failure Lab'}</b><small>{job.id}</small></div></div></td><td><JobStatus status={job.status}/></td><td>{job.attempts} / {job.maxAttempts}</td><td>{new Date(job.createdAt).toLocaleString()}</td><td><div className="job-actions">{job.status==='SUCCEEDED'&&job.result?.fileName&&<button title="Download result" onClick={()=>onDownload(job)}><Download size={15}/></button>}{['QUEUED','SCHEDULED','RETRY_WAIT'].includes(job.status)&&<button title="Cancel job" onClick={()=>onCancel(job.id)}><X size={15}/></button>}{job.status==='DEAD'&&<button title="Retry job (admin)" onClick={()=>onRetry(job.id)}><RefreshCw size={15}/></button>}</div></td></tr>)}</tbody></table></div>
}
function JobStatus({status}:{status:string}) {const cls=status==='SUCCEEDED'?'status-success':['DEAD','CANCELLED'].includes(status)?'status-danger':['RUNNING','QUEUED'].includes(status)?'status-info':'status-warn';return <span className={`job-status ${cls}`}><span/>{status.replace('_',' ')}</span>}
function EmptyState({title,body}:{title:string;body:string}) {return <div className="empty-state"><span><ListTodo size={20}/></span><b>{title}</b><p>{body}</p></div>}

function LocalDemoWorkspace() {
  const [open, setOpen] = useState(false)
  if(open)return <Dashboard localPreview />
  return <LandingPage onDemo={()=>setOpen(true)} />
}
export default function App() { return <LocalDemoWorkspace /> }

function AuthenticatedWorkspace() { const { getToken } = useAuth(); return <Dashboard getToken={getToken} onSignOut={() => {}}/> }
export function AuthenticatedApp() { const { isLoaded, isSignedIn } = useUser(); if(!isLoaded)return <main className="loading-screen"><div className="loading-mark"><BrandMark/></div><div className="loading-ring"/><p>Preparing your workspace…</p></main>; return isSignedIn?<AuthenticatedWorkspace/>:<LandingPage live/> }
