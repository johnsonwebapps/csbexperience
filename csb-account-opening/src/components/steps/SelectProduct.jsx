import { useState } from 'react'

const PRODUCTS = [
  {
    id: 'business-checking',
    category: 'Checking',
    name: 'Business Checking',
    tagline: 'Everyday banking made simple',
    icon: '🏦',
    features: [
      'No monthly maintenance fee with $1,500 avg. daily balance',
      'Unlimited electronic transactions',
      'Free online and mobile banking',
      'Free first order of business checks',
      'Debit Mastercard® included',
    ],
    minDeposit: '$100',
    apy: null,
    popular: true,
  },
  {
    id: 'business-interest-checking',
    category: 'Checking',
    name: 'Business Interest Checking',
    tagline: 'Earn interest while managing cash flow',
    icon: '💰',
    features: [
      'Earn interest on your balance',
      'Unlimited electronic transactions',
      'Free online and mobile banking',
      'Debit Mastercard® included',
      'Ideal for partnerships & LLCs',
    ],
    minDeposit: '$500',
    apy: 'Up to 0.10%',
    popular: false,
  },
  {
    id: 'business-savings',
    category: 'Savings',
    name: 'Business Savings',
    tagline: 'Grow your business reserves',
    icon: '🐷',
    features: [
      'Competitive interest rate',
      'No monthly fee with $300 minimum balance',
      'Up to 6 withdrawals per statement period',
      'FDIC insured up to $250,000',
      'Link to Business Checking for overdraft protection',
    ],
    minDeposit: '$300',
    apy: 'Up to 0.15%',
    popular: false,
  },
  {
    id: 'business-money-market',
    category: 'Savings',
    name: 'Business Money Market',
    tagline: 'Higher yields for larger balances',
    icon: '📊',
    features: [
      'Tiered interest rates – earn more as balances grow',
      'Check writing and debit access',
      'Free online and mobile banking',
      'Ideal for operating reserves',
      'FDIC insured up to $250,000',
    ],
    minDeposit: '$2,500',
    apy: 'Up to 0.40%',
    popular: false,
  },
  {
    id: 'business-cd',
    category: 'CD',
    name: 'Business Certificate of Deposit',
    tagline: 'Lock in a guaranteed rate',
    icon: '📅',
    features: [
      'Terms from 3 months to 5 years',
      'Guaranteed fixed rate for the full term',
      'FDIC insured up to $250,000',
      'Automatic renewal option',
      "Great for surplus funds you won't need immediately",
    ],
    minDeposit: '$1,000',
    apy: 'Up to 4.50%',
    popular: false,
  },
]

const CATEGORIES = ['All', 'Checking', 'Savings', 'CD']

export default function SelectProduct({ formData, updateFormData, onNext, onBack }) {
  const [filter, setFilter] = useState('All')
  const [error, setError] = useState('')

  const filtered = PRODUCTS.filter(p => filter === 'All' || p.category === filter)

  const handleNext = () => {
    if (!formData.selectedProduct) {
      setError('Please select an account type to continue.')
      return
    }
    setError('')
    onNext()
  }

  return (
    <div className="space-y-6">
      <div className="card">
        <h2 className="section-header">Choose Your Account</h2>
        <p className="text-gray-500 text-sm">
          Select the account that best fits your business needs. You can open additional accounts later.
        </p>
      </div>

      {/* Filter tabs */}
      <div className="flex gap-2 flex-wrap">
        {CATEGORIES.map(cat => (
          <button
            key={cat}
            onClick={() => setFilter(cat)}
            className={`px-4 py-1.5 rounded-full text-sm font-semibold transition-all ${
              filter === cat
                ? 'bg-csb-navy text-white'
                : 'bg-white text-gray-600 border border-gray-200 hover:border-csb-navy hover:text-csb-navy'
            }`}
          >
            {cat}
          </button>
        ))}
      </div>

      {/* Product cards */}
      <div className="grid gap-4">
        {filtered.map(product => (
          <div
            key={product.id}
            className={`product-card relative cursor-pointer ${formData.selectedProduct === product.id ? 'selected' : ''}`}
            onClick={() => { updateFormData({ selectedProduct: product.id }); setError('') }}
            role="radio"
            aria-checked={formData.selectedProduct === product.id}
            tabIndex={0}
            onKeyDown={e => {
              if (e.key === 'Enter' || e.key === ' ') {
                updateFormData({ selectedProduct: product.id })
                setError('')
              }
            }}
          >
            {product.popular && (
              <div className="absolute top-3 right-3 bg-csb-gold text-csb-navy text-xs font-bold px-2 py-0.5 rounded-full">
                Most Popular
              </div>
            )}
            <div className="flex items-start gap-4">
              <div className="text-3xl flex-shrink-0 mt-0.5">{product.icon}</div>
              <div className="flex-1 min-w-0">
                <div className="flex items-start justify-between gap-2">
                  <div>
                    <span className="text-xs text-csb-green font-semibold uppercase tracking-wide">{product.category}</span>
                    <h3 className="font-bold text-gray-900 text-lg leading-tight">{product.name}</h3>
                    <p className="text-sm text-gray-500">{product.tagline}</p>
                  </div>
                  <div className={`mt-1 w-5 h-5 rounded-full border-2 flex items-center justify-center flex-shrink-0 transition-all ${
                    formData.selectedProduct === product.id ? 'border-csb-navy' : 'border-gray-300'
                  }`}>
                    {formData.selectedProduct === product.id && (
                      <div className="w-2.5 h-2.5 rounded-full bg-csb-navy" />
                    )}
                  </div>
                </div>
                <div className="flex gap-6 mt-3 mb-3 text-sm">
                  <div>
                    <span className="text-gray-400 text-xs uppercase tracking-wide">Min. Deposit</span>
                    <div className="font-bold text-csb-navy">{product.minDeposit}</div>
                  </div>
                  {product.apy && (
                    <div>
                      <span className="text-gray-400 text-xs uppercase tracking-wide">APY</span>
                      <div className="font-bold text-csb-green">{product.apy}</div>
                    </div>
                  )}
                </div>
                <ul className="space-y-1">
                  {product.features.map((f, i) => (
                    <li key={i} className="flex items-start gap-2 text-sm text-gray-600">
                      <span className="text-csb-green font-bold flex-shrink-0 mt-0.5">✓</span>
                      {f}
                    </li>
                  ))}
                </ul>
              </div>
            </div>
          </div>
        ))}
      </div>

      {error && <p className="error-text">{error}</p>}

      <div className="flex justify-between gap-3">
        <button className="btn-secondary" onClick={onBack}>← Back</button>
        <button className="btn-primary" onClick={handleNext}>Continue →</button>
      </div>
    </div>
  )
}
